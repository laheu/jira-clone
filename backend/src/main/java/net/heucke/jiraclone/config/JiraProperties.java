package net.heucke.jiraclone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Access to Jira resources outside the database.
 *
 * @param attachmentsDir path to Jira's attachment store
 *                       ({@code <jira-home>/data/attachments}); empty or null
 *                       disables attachment downloads.
 */
@ConfigurationProperties(prefix = "app.jira")
public record JiraProperties(String attachmentsDir) {

    public boolean attachmentsEnabled() {
        return attachmentsDir != null && !attachmentsDir.isBlank();
    }
}
