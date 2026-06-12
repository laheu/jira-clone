package net.heucke.jiraclone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The single local user of this application. The password uses Spring
 * Security's encoder-prefix format, e.g. "{bcrypt}$2a$10$..." or "{noop}secret".
 */
@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(String username, String password) {
}
