package net.heucke.jiraclone.repo;

import java.sql.Timestamp;

/**
 * Raw query results, close to the Jira schema. User references are still
 * user keys here and get resolved to display names by the service layer.
 */
public final class Rows {

    private Rows() {
    }

    public record IssueRow(long id, long issueNum, long projectId, String projectKey,
                           String summary, String description,
                           String typeId, String typeName,
                           String statusId, String statusName, Integer statusCategory,
                           String priorityId, String priorityName, String priorityColor,
                           String resolutionName,
                           String assigneeKey, String reporterKey,
                           Timestamp created, Timestamp updated, Timestamp dueDate, Timestamp resolutionDate,
                           Long workflowId) {

        public String issueKey() {
            return projectKey + "-" + issueNum;
        }
    }

    public record ProjectRow(long id, String key, String name, String description,
                             String leadKey, long issueCount) {
    }

    public record CommentRow(long id, String authorKey, String body, Timestamp created, Timestamp updated) {
    }

    public record UserRow(String key, String displayName, String email) {
    }

    public record StatusRow(String id, String name, Integer category) {
    }

    public record AttachmentRow(long id, long issueId, String filename, String mimeType,
                                long fileSize, String authorKey, Timestamp created,
                                String projectKey, long issueNum) {

        public String issueKey() {
            return projectKey + "-" + issueNum;
        }
    }

    public record TypeRow(String id, String name) {
    }

    public record PriorityRow(String id, String name, String color) {
    }
}
