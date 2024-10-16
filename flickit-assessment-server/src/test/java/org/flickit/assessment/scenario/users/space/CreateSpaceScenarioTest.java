package org.flickit.assessment.scenario.users.space;

import org.flickit.assessment.helper.auth.JwtTokenTestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
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

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateSpaceScenarioTest {

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
    @DisplayName("The application should connect to testContainers")
    void testDatabaseConnection() {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        assertEquals("jdbc:postgresql://localhost:" + postgreSQLContainer.getMappedPort
            (5432) + "/"+postgreSQLContainer.getDatabaseName()+"?loggerLevel=OFF", jdbcUrl);
    }

    @Test
    @DisplayName("Creating space with valid parameters in the database should be done successfully.")
    void testCreateSpace_validParameters_successful() throws JSONException, SQLException {
        String title = "test";
        String space = new JSONObject().put("title", title).toString();
        String authenticationToken = JwtTokenTestUtils.generateJwtToken(UUID.randomUUID());

        var conn = DriverManager.getConnection(postgreSQLContainer.getJdbcUrl(),
            postgreSQLContainer.getUsername(),
            postgreSQLContainer.getPassword());
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT * from public.fau_space");
        resultSet.next();
        int rowCount = resultSet.getRow();
        then(rowCount).isEqualTo(0);
        then(resultSet.next()).isEqualTo(false);

        ResponseEntity<String> response = whenCreateSpace(space, authenticationToken);

        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        resultSet = conn.createStatement().executeQuery("SELECT * from public.fau_space");
        resultSet.next();
        rowCount = resultSet.getRow();
        then(rowCount).isEqualTo(1);
        then(resultSet.next()).isEqualTo(false);
    }

    @Test
    @DisplayName("Creating a space with an empty request body, should cause a BadRequest error.")
    void testCreateSpace_emptyBody_BadRequest() {
        String space = new JSONObject().toString();
        String authenticationToken = JwtTokenTestUtils.generateJwtToken(UUID.randomUUID());

        ResponseEntity<String> response = whenCreateSpace(space, authenticationToken);

        then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Creating a space with invalid authentication token, should cause an UNAUTHORIZED error.")
    void testCreateSpace_incorrectAuthenticationToken_BadRequest() throws JSONException {
        String title = "test";
        String space = new JSONObject().put("title", title).toString();
        String authenticationToken = "blah blah";

        ResponseEntity<String> response = whenCreateSpace(space, authenticationToken);

        then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> whenCreateSpace(String space, String authenticationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", authenticationToken);
        HttpEntity<String> request = new HttpEntity<>(space, headers);

        return testRestTemplate.exchange(
            ENDPOINT,
            HttpMethod.POST,
            request,
            String.class
        );
    }
}
