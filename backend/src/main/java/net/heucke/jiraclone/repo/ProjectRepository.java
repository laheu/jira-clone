package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.repo.Rows.ProjectRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private static final String SELECT = """
            SELECT p.id, p.pkey, p.pname, p.description, p.lead,
                   (SELECT COUNT(*) FROM jiraissue i WHERE i.project = p.id) AS issue_count
            FROM project p
            """;

    private static final RowMapper<ProjectRow> MAPPER = (rs, rowNum) -> new ProjectRow(
            rs.getLong("id"),
            rs.getString("pkey"),
            rs.getString("pname"),
            rs.getString("description"),
            rs.getString("lead"),
            rs.getLong("issue_count"));

    private final JdbcTemplate jdbc;

    public ProjectRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ProjectRow> findAll() {
        return jdbc.query(SELECT + " ORDER BY p.pname", MAPPER);
    }

    public Optional<ProjectRow> findByKey(String key) {
        List<ProjectRow> rows = jdbc.query(SELECT + " WHERE p.pkey = ?",
                MAPPER, key.toUpperCase(Locale.ROOT));
        return rows.stream().findFirst();
    }
}
