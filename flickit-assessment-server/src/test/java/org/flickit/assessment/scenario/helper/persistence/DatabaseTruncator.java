package org.flickit.assessment.scenario.helper.persistence;

import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DatabaseTruncator {

    private final JdbcTemplate jdbcTemplate;

    private static final String PERSISTENCE_BASE_PACKAGE = "org.flickit.assessment.data.jpa.core";

    public void truncateTables() {
        List<String> tableNames = extractTableNames();
        log.debug("Truncating tables {}", tableNames);
        for (String tableName : tableNames) {
            String sql = "TRUNCATE TABLE " + tableName + " CASCADE";
            jdbcTemplate.execute(sql);
        }
    }

    public List<String> extractTableNames() {
        Reflections reflections = new Reflections(PERSISTENCE_BASE_PACKAGE, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
            .stream()
            .filter(x -> x.getName().contains("JpaEntity"))
            .filter(x -> x.getAnnotation(Table.class) != null)
            .map(x -> x.getAnnotation(Table.class).name())
            .toList();
    }
}
