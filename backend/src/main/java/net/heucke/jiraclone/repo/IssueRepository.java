package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.repo.Rows.IssueRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Read-only access to {@code jiraissue} and related lookup tables.
 * All SQL targets the original Jira 10.x schema (Oracle); the dev profile
 * runs the same statements against H2 in Oracle compatibility mode.
 */
@Repository
public class IssueRepository {

    private static final String SELECT = """
            SELECT i.id, i.issuenum, p.id AS project_id, p.pkey AS project_key,
                   i.summary, i.description,
                   i.issuetype AS type_id, t.pname AS type_name,
                   i.issuestatus AS status_id, s.pname AS status_name, s.statuscategory AS status_category,
                   i.priority AS priority_id, pr.pname AS priority_name, pr.status_color AS priority_color,
                   r.pname AS resolution_name,
                   i.assignee, i.reporter,
                   i.created, i.updated, i.duedate, i.resolutiondate, i.workflow_id
            FROM jiraissue i
            JOIN project p ON p.id = i.project
            LEFT JOIN issuetype t ON t.id = i.issuetype
            LEFT JOIN issuestatus s ON s.id = i.issuestatus
            LEFT JOIN priority pr ON pr.id = i.priority
            LEFT JOIN resolution r ON r.id = i.resolution
            """;

    private static final RowMapper<IssueRow> MAPPER = (rs, rowNum) -> new IssueRow(
            rs.getLong("id"),
            rs.getLong("issuenum"),
            rs.getLong("project_id"),
            rs.getString("project_key"),
            rs.getString("summary"),
            rs.getString("description"),
            rs.getString("type_id"),
            rs.getString("type_name"),
            rs.getString("status_id"),
            rs.getString("status_name"),
            toInteger(rs.getObject("status_category")),
            rs.getString("priority_id"),
            rs.getString("priority_name"),
            rs.getString("priority_color"),
            rs.getString("resolution_name"),
            rs.getString("assignee"),
            rs.getString("reporter"),
            rs.getTimestamp("created"),
            rs.getTimestamp("updated"),
            rs.getTimestamp("duedate"),
            rs.getTimestamp("resolutiondate"),
            toLong(rs.getObject("workflow_id")));

    private final JdbcTemplate jdbc;

    public IssueRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<IssueRow> search(String projectKey, String statusId, String typeId, String text,
                                 int offset, int limit) {
        StringBuilder sql = new StringBuilder(SELECT);
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, projectKey, statusId, typeId, text);
        sql.append(" ORDER BY i.updated DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);
        return jdbc.query(sql.toString(), MAPPER, params.toArray());
    }

    public long count(String projectKey, String statusId, String typeId, String text) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM jiraissue i JOIN project p ON p.id = i.project\n");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, projectKey, statusId, typeId, text);
        Long count = jdbc.queryForObject(sql.toString(), Long.class, params.toArray());
        return count == null ? 0 : count;
    }

    public Optional<IssueRow> findByKey(String projectKey, long issueNum) {
        List<IssueRow> rows = jdbc.query(SELECT + " WHERE p.pkey = ? AND i.issuenum = ?",
                MAPPER, projectKey.toUpperCase(Locale.ROOT), issueNum);
        return rows.stream().findFirst();
    }

    public List<IssueRow> findByIds(java.util.Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(", ", java.util.Collections.nCopies(ids.size(), "?"));
        return jdbc.query(SELECT + " WHERE i.id IN (" + placeholders + ") ORDER BY i.issuenum",
                MAPPER, ids.toArray());
    }

    /** System labels of an issue (custom label fields have a fieldid and are out of scope). */
    public List<String> findLabels(long issueId) {
        return jdbc.queryForList(
                "SELECT label FROM label WHERE issue = ? AND fieldid IS NULL ORDER BY label",
                String.class, issueId);
    }

    private static void appendFilters(StringBuilder sql, List<Object> params,
                                      String projectKey, String statusId, String typeId, String text) {
        sql.append(" WHERE p.pkey = ?");
        params.add(projectKey.toUpperCase(Locale.ROOT));
        if (statusId != null && !statusId.isBlank()) {
            sql.append(" AND i.issuestatus = ?");
            params.add(statusId);
        }
        if (typeId != null && !typeId.isBlank()) {
            sql.append(" AND i.issuetype = ?");
            params.add(typeId);
        }
        if (text != null && !text.isBlank()) {
            sql.append(" AND LOWER(i.summary) LIKE ?");
            params.add("%" + text.toLowerCase(Locale.ROOT) + "%");
        }
    }

    private static Integer toInteger(Object value) {
        return value instanceof Number n ? n.intValue() : null;
    }

    private static Long toLong(Object value) {
        return value instanceof Number n ? n.longValue() : null;
    }
}
