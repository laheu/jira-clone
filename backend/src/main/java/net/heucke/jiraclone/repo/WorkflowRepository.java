package net.heucke.jiraclone.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Resolves which workflow applies to an issue and reads its OSWorkflow
 * descriptor. Note: the built-in default workflow "jira" is not stored in
 * the database, so issues using it simply yield no transitions.
 */
@Repository
public class WorkflowRepository {

    private final JdbcTemplate jdbc;

    public WorkflowRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Long> findWorkflowSchemeId(long projectId) {
        List<Long> ids = jdbc.queryForList("""
                        SELECT sink_node_id FROM nodeassociation
                        WHERE source_node_entity = 'Project'
                          AND sink_node_entity = 'WorkflowScheme'
                          AND association_type = 'ProjectScheme'
                          AND source_node_id = ?
                        """,
                Long.class, projectId);
        return ids.stream().findFirst();
    }

    public Optional<String> findWorkflowName(long schemeId, String issueTypeId) {
        List<String> names = jdbc.queryForList(
                "SELECT workflow FROM workflowschemeentity WHERE scheme = ? AND issuetype = ?",
                String.class, schemeId, issueTypeId);
        if (names.isEmpty()) {
            // '0' is Jira's marker for the scheme's default workflow
            names = jdbc.queryForList(
                    "SELECT workflow FROM workflowschemeentity WHERE scheme = ? AND issuetype = '0'",
                    String.class, schemeId);
        }
        return names.stream().findFirst();
    }

    public Optional<String> findDescriptor(String workflowName) {
        List<String> descriptors = jdbc.queryForList(
                "SELECT descriptor FROM jiraworkflows WHERE workflowname = ?",
                String.class, workflowName);
        return descriptors.stream().findFirst();
    }

    public Optional<Integer> findCurrentStepId(long workflowEntryId) {
        List<Integer> steps = jdbc.queryForList(
                "SELECT step_id FROM os_currentstep WHERE entry_id = ? ORDER BY id",
                Integer.class, workflowEntryId);
        return steps.stream().findFirst();
    }
}
