package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.repo.Rows.CommentRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbc;

    public CommentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CommentRow> findByIssueId(long issueId) {
        return jdbc.query("""
                        SELECT id, author, actionbody, created, updated
                        FROM jiraaction
                        WHERE issueid = ? AND actiontype = 'comment'
                        ORDER BY created
                        """,
                (rs, rowNum) -> new CommentRow(
                        rs.getLong("id"),
                        rs.getString("author"),
                        rs.getString("actionbody"),
                        rs.getTimestamp("created"),
                        rs.getTimestamp("updated")),
                issueId);
    }
}
