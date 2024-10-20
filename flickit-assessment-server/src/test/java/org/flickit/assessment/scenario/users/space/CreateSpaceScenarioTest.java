package org.flickit.assessment.scenario.users.space;

import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.scenario.data.config.PostgresTestContainerHolder;
import org.flickit.assessment.scenario.fixture.auth.JwtTokenTestUtils;
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
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateSpaceScenarioTest {

    private static final String ENDPOINT = "/spaces";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private SpaceJpaRepository spaceRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        PostgresTestContainerHolder.setProperties(registry);
    }

    @Test
    @DisplayName("Creating space with valid parameters in the database should be done successfully.")
    void testCreateSpace_validParameters_successful() throws JSONException {
        String title = "test";
        String space = new JSONObject().put("title", title).toString();
        String authenticationToken = JwtTokenTestUtils.generateJwtToken(UUID.randomUUID());

        int spacesCount = spaceRepository.findAll().size();

        ResponseEntity<String> response = whenCreateSpace(space, authenticationToken);

        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(spaceRepository.findAll().size()).isEqualTo(spacesCount + 1);
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
