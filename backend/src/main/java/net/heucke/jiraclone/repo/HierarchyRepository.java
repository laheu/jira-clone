package net.heucke.jiraclone.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolves the issue hierarchy (e.g. Initiative > Epic > Story > Sub-task).
 * Jira stores parent/child relations in three different places, all of which
 * are considered here:
 * <ul>
 *   <li>issue links with a hierarchical link type (Epic-Story link, sub-task link)</li>
 *   <li>the "Epic Link" custom field (issue id in {@code numbervalue})</li>
 *   <li>the Advanced Roadmaps "Parent Link" custom field (issue id in {@code stringvalue})</li>
 * </ul>
 */
@Repository
public class HierarchyRepository {

    private static final String HIERARCHICAL_LINK_TYPE = """
            (lt.pstyle IN ('jira_subtask', 'jira_gh_epic_story')
             OR lt.linkname IN ('Epic-Story Link', 'Parent-Child Link'))
            """;

    private static final String EPIC_LINK_KEY = "com.pyxis.greenhopper.jira:gh-epic-link";
    private static final String PARENT_LINK_KEY = "com.atlassian.jpo:jpo-custom-field-parent";

    private final JdbcTemplate jdbc;

    public HierarchyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Long> findParentIds(long issueId) {
        Set<Long> ids = new LinkedHashSet<>();
        ids.addAll(jdbc.queryForList("""
                        SELECT il.source FROM issuelink il
                        JOIN issuelinktype lt ON lt.id = il.linktype
                        WHERE il.destination = ? AND """ + HIERARCHICAL_LINK_TYPE,
                Long.class, issueId));
        ids.addAll(jdbc.queryForList("""
                        SELECT cv.numbervalue FROM customfieldvalue cv
                        JOIN customfield cf ON cf.id = cv.customfield
                        WHERE cv.issue = ? AND cf.customfieldtypekey = ? AND cv.numbervalue IS NOT NULL
                        """,
                Long.class, issueId, EPIC_LINK_KEY));
        for (String value : jdbc.queryForList("""
                        SELECT cv.stringvalue FROM customfieldvalue cv
                        JOIN customfield cf ON cf.id = cv.customfield
                        WHERE cv.issue = ? AND cf.customfieldtypekey = ? AND cv.stringvalue IS NOT NULL
                        """,
                String.class, issueId, PARENT_LINK_KEY)) {
            try {
                ids.add(Long.parseLong(value.trim()));
            } catch (NumberFormatException ignored) {
                // unexpected parent link payload, skip
            }
        }
        return List.copyOf(ids);
    }

    public List<Long> findChildIds(long issueId) {
        Set<Long> ids = new LinkedHashSet<>();
        ids.addAll(jdbc.queryForList("""
                        SELECT il.destination FROM issuelink il
                        JOIN issuelinktype lt ON lt.id = il.linktype
                        WHERE il.source = ? AND """ + HIERARCHICAL_LINK_TYPE,
                Long.class, issueId));
        ids.addAll(jdbc.queryForList("""
                        SELECT cv.issue FROM customfieldvalue cv
                        JOIN customfield cf ON cf.id = cv.customfield
                        WHERE cf.customfieldtypekey = ? AND cv.numbervalue = ?
                        """,
                Long.class, EPIC_LINK_KEY, issueId));
        ids.addAll(jdbc.queryForList("""
                        SELECT cv.issue FROM customfieldvalue cv
                        JOIN customfield cf ON cf.id = cv.customfield
                        WHERE cf.customfieldtypekey = ? AND cv.stringvalue = ?
                        """,
                Long.class, PARENT_LINK_KEY, String.valueOf(issueId)));
        return List.copyOf(ids);
    }
}
