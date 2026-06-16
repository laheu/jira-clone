package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.repo.Rows.AttachmentRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AttachmentRepository {

    private static final String SELECT = """
            SELECT fa.id, fa.issueid, fa.filename, fa.mimetype, fa.filesize, fa.author, fa.created,
                   p.pkey AS project_key, i.issuenum
            FROM fileattachment fa
            JOIN jiraissue i ON i.id = fa.issueid
            JOIN project p ON p.id = i.project
            """;

    private static final RowMapper<AttachmentRow> MAPPER = (rs, rowNum) -> new AttachmentRow(
            rs.getLong("id"),
            rs.getLong("issueid"),
            rs.getString("filename"),
            rs.getString("mimetype"),
            rs.getLong("filesize"),
            rs.getString("author"),
            rs.getTimestamp("created"),
            rs.getString("project_key"),
            rs.getLong("issuenum"));

    private final JdbcTemplate jdbc;

    public AttachmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<AttachmentRow> findByIssueId(long issueId) {
        return jdbc.query(SELECT + " WHERE fa.issueid = ? ORDER BY fa.created", MAPPER, issueId);
    }

    public Optional<AttachmentRow> findById(long attachmentId) {
        return jdbc.query(SELECT + " WHERE fa.id = ?", MAPPER, attachmentId).stream().findFirst();
    }
}
