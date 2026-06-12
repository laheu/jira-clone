package net.heucke.jiraclone.repo;

import net.heucke.jiraclone.repo.Rows.PriorityRow;
import net.heucke.jiraclone.repo.Rows.StatusRow;
import net.heucke.jiraclone.repo.Rows.TypeRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MetaRepository {

    private final JdbcTemplate jdbc;

    public MetaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StatusRow> listStatuses() {
        return jdbc.query("SELECT id, pname, statuscategory FROM issuestatus ORDER BY sequence",
                (rs, rowNum) -> new StatusRow(
                        rs.getString("id"),
                        rs.getString("pname"),
                        rs.getObject("statuscategory") instanceof Number n ? n.intValue() : null));
    }

    public Map<String, StatusRow> statusById() {
        return listStatuses().stream()
                .collect(Collectors.toMap(StatusRow::id, Function.identity(), (a, b) -> a));
    }

    public List<TypeRow> listTypes() {
        return jdbc.query("SELECT id, pname FROM issuetype ORDER BY sequence",
                (rs, rowNum) -> new TypeRow(rs.getString("id"), rs.getString("pname")));
    }

    public List<PriorityRow> listPriorities() {
        return jdbc.query("SELECT id, pname, status_color FROM priority ORDER BY sequence",
                (rs, rowNum) -> new PriorityRow(
                        rs.getString("id"), rs.getString("pname"), rs.getString("status_color")));
    }
}
