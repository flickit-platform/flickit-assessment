package org.flickit.assessment.scenario.data.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class TestDatabaseContainerConnectionTest {

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        PostgresTestContainerHolder.setProperties(registry);
    }

    @Test
    @DisplayName("The application should connect to testContainers")
    void testDatabaseConnection() {
        var postgreSQLContainer = PostgresTestContainerHolder.getInstance();
        assertTrue(postgreSQLContainer.isRunning());
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        assertEquals("jdbc:postgresql://localhost:" + postgreSQLContainer.getMappedPort
            (5432) + "/" + postgreSQLContainer.getDatabaseName() + "?loggerLevel=OFF", jdbcUrl);
    }
}
