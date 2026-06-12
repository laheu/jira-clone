package net.heucke.jiraclone.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads business custom field values of an issue. Select-style fields store
 * the id of a {@code customfieldoption} row in {@code stringvalue}; user
 * pickers store a user key. Both are resolved by the service layer.
 */
@Repository
public class CustomFieldRepository {

    public record CustomFieldValueRow(long fieldId, String name, String typeKey,
                                      String stringValue, BigDecimal numberValue,
                                      String textValue, Timestamp dateValue) {
    }

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public CustomFieldRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    public List<CustomFieldValueRow> findValues(long issueId) {
        return jdbc.query("""
                        SELECT cf.id AS field_id, cf.cfname, cf.customfieldtypekey,
                               cv.stringvalue, cv.numbervalue, cv.textvalue, cv.datevalue
                        FROM customfieldvalue cv
                        JOIN customfield cf ON cf.id = cv.customfield
                        WHERE cv.issue = ?
                        ORDER BY cf.cfname, cv.id
                        """,
                (rs, rowNum) -> new CustomFieldValueRow(
                        rs.getLong("field_id"),
                        rs.getString("cfname"),
                        rs.getString("customfieldtypekey"),
                        rs.getString("stringvalue"),
                        rs.getBigDecimal("numbervalue"),
                        rs.getString("textvalue"),
                        rs.getTimestamp("datevalue")),
                issueId);
    }

    /** Option id (as string, like it is stored in stringvalue) to display value. */
    public Map<String, String> findOptions(Collection<Long> fieldIds) {
        if (fieldIds.isEmpty()) {
            return Map.of();
        }
        Map<String, String> options = new HashMap<>();
        namedJdbc.query("""
                        SELECT id, customvalue FROM customfieldoption
                        WHERE customfield IN (:fieldIds)
                        """,
                new MapSqlParameterSource("fieldIds", fieldIds),
                rs -> {
                    options.put(rs.getString("id"), rs.getString("customvalue"));
                });
        return options;
    }
}
