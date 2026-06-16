package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.repo.Rows.UserRow;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves Jira user keys ({@code jiraissue.assignee} etc., stored in
 * {@code app_user.user_key}) to display name and email from {@code cwd_user}.
 */
@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public UserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, UserRow> resolve(Collection<String> userKeys) {
        Set<String> keys = new HashSet<>(userKeys);
        keys.remove(null);
        if (keys.isEmpty()) {
            return Map.of();
        }
        // MIN() collapses duplicates when a user exists in several Crowd directories.
        return jdbc.query("""
                                SELECT au.user_key, au.lower_user_name,
                                       MIN(cu.display_name) AS display_name,
                                       MIN(cu.email_address) AS email_address
                                FROM app_user au
                                LEFT JOIN cwd_user cu ON cu.lower_user_name = au.lower_user_name
                                WHERE au.user_key IN (:keys)
                                GROUP BY au.user_key, au.lower_user_name
                                """,
                        new MapSqlParameterSource("keys", keys),
                        (rs, rowNum) -> {
                            String displayName = rs.getString("display_name");
                            return new UserRow(
                                    rs.getString("user_key"),
                                    displayName != null ? displayName : rs.getString("lower_user_name"),
                                    rs.getString("email_address"));
                        })
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(UserRow::key, Function.identity(), (a, b) -> a));
    }
}
