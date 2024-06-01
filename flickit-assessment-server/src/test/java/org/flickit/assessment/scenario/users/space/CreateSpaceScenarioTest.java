package org.flickit.assessment.scenario.users.space;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateSpaceScenarioTest {
    String Auth = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJWRmtJYmU0V1U4SGV3Wml0aHdFbmZqU0xUcjJJdk1tRnNKYWRndmh1WU13In0.eyJleHAiOjE3MDU3NTEzMTIsImlhdCI6MTcwNTc1MTAxMiwiYXV0aF90aW1lIjoxNzA1NzUxMDExLCJqdGkiOiJlZjk0NjA5NC1mNDIwLTQwNDktYjEyYy1mNTVkNmZjNzQwODAiLCJpc3MiOiJodHRwczovL3Rlc3QuZmxpY2tpdC5vcmcvYWNjb3VudHMvcmVhbG1zL2ZsaWNraXQiLCJhdWQiOlsiZmxpY2tpdC1iZmYiLCJhY2NvdW50Il0sInN1YiI6ImVkYzJlMjk1LTI5NjQtNGNmYi1hMDZhLTVlYjIyMTU4NmQyOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZsaWNraXQtZnJvbnRlbmQiLCJub25jZSI6IjI3ODA0MDFiLWM2YzYtNDI0MS04MmU1LTZmN2IwMTFiMzQ2YSIsInNlc3Npb25fc3RhdGUiOiJjNTU2NDUwMy04ODRmLTQyNzAtODFhNi1jZjBiOTk4YmQ4NGUiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vdGVzdC5mbGlja2l0Lm9yZyIsImh0dHA6Ly9sb2NhbGhvc3Q6MzAwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1mbGlja2l0Iiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiJjNTU2NDUwMy04ODRmLTQyNzAtODFhNi1jZjBiOTk4YmQ4NGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IkFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW5AYXN0YS5pciIsImdpdmVuX25hbWUiOiJBZG1pbiIsImVtYWlsIjoiYWRtaW5AYXN0YS5pciJ9.Zfl_zcjvAcyG5QxesPz1IXRDLE-3vbk3fYvNSEYraagD3CeRpNyBNm62nV65inWATNDqRtaegefBG_9LvbEPNc1I2Qi0Rp6YYVC4xysV28_GUHOfRgtoCFndhgM4lWBQl6JfcUwzLOOMIyP4a_lqw6ESixK14yRByo-vY3ejIDktMcgGkixuYVkXtS5VCbccH7i_jyxcXdanvFGi94j_OaxzxwoiY39cQHjv5WkEVIkVC0VxIuBDS4Z2LjPkcPdjuo-j2vW_HKXyT5q9FE0TXMOeXAUu3rYxDseMfSYbF719ZmjcFHAM-1IDtMfZcIHzVCT8IQbnCpLPID0xHjowKA";

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String ENDPOINT = "/spaces";
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
        .withDatabaseName("flickit")
        .withUsername("flickit")
        .withPassword("flickit")
        .withInitScript("init.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.liquibase.enabled", () -> false);

    }

    @Test
    void testDatabaseConnection() {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        assertEquals("jdbc:postgresql://localhost:" + postgreSQLContainer.getMappedPort(5432) + "/flickit?loggerLevel=OFF", jdbcUrl);
    }

    @Test
    void createSpace() {
        String title = "test";

        ResponseEntity<String> response = whenCreateSpace();

        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private ResponseEntity<String> whenCreateSpace() {
        String json = "{\"title\":\"test\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", Auth);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        return testRestTemplate.exchange(
            ENDPOINT,
            HttpMethod.POST,
            request,
            String.class
        );
    }

}
