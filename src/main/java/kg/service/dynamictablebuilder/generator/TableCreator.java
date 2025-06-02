package kg.service.dynamictablebuilder.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TableCreator {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void createTable(String createTableSql) {
        jdbcTemplate.execute(createTableSql);
    }

    @Transactional
    public void query(String sql) {
        jdbcTemplate.execute(sql);
    }

    @Transactional
    public void query(String sql, Object[] args) {
        jdbcTemplate.update(sql, args);
    }
}
