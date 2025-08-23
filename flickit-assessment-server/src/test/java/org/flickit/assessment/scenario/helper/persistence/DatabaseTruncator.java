package org.flickit.assessment.scenario.helper.persistence;

import com.google.common.reflect.ClassPath;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DatabaseTruncator {

    private final JdbcTemplate jdbcTemplate;

    private static final String PERSISTENCE_BASE_PACKAGE = "org.flickit.assessment.data.jpa";
    private static final Set<String> TABLE_NAMES = extractTableNames();

    public void truncateTables() {
        for (String tableName : TABLE_NAMES) {
            log.debug("Truncating table {}", tableName);
            String sql = "TRUNCATE TABLE " + tableName + " CASCADE";
            jdbcTemplate.execute(sql);
        }
    }

    @SneakyThrows
    public static Set<String> extractTableNames() {
        ClassPath classpath = ClassPath.from(ClassLoader.getSystemClassLoader());
        return classpath.getTopLevelClassesRecursive(PERSISTENCE_BASE_PACKAGE).stream()
            .map(ClassPath.ClassInfo::load)
            .filter(cls -> cls.getName().contains("JpaEntity"))
            .filter(cls -> cls.isAnnotationPresent(Table.class))
            .map(cls -> cls.getAnnotation(Table.class).name())
            .collect(toSet());
    }
}
