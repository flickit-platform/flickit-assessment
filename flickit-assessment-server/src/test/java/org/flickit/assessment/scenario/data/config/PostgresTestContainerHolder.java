package org.flickit.assessment.scenario.data.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class PostgresTestContainerHolder {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("flickit")
            .withUsername("flickit")
            .withPassword("flickit")
            .withInitScript("init.sql");

        POSTGRESQL_CONTAINER.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return POSTGRESQL_CONTAINER;
    }

    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.liquibase.enabled", () -> false);
    }
}