package org.flickit.assessment.scenario.test;

import io.restassured.RestAssured;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockWebServer;
import org.flickit.assessment.scenario.data.config.MinioTestContainerHolder;
import org.flickit.assessment.scenario.data.config.PostgresTestContainerHolder;
import org.flickit.assessment.scenario.helper.persistence.DatabaseTruncator;
import org.flickit.assessment.scenario.helper.persistence.JpaTestTemplate;
import org.flickit.assessment.scenario.test.users.user.UserTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.flickit.assessment.scenario.fixture.request.CreateUserRequestDtoMother.createUserRequestDto;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractScenarioTest {

    protected ScenarioContext context;

    @Autowired
    protected JpaTestTemplate jpaTemplate;

    @Autowired
    protected DatabaseTruncator databaseTruncator;

    @Autowired
    protected UserTestHelper userHelper;

    @LocalServerPort
    private Integer port;

    protected MockWebServer mockDslWebServer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        PostgresTestContainerHolder.setProperties(registry);
        MinioTestContainerHolder.setProperties(registry);
    }

    @BeforeEach
    @SneakyThrows
    void setup() {
        RestAssured.port = port;
        if (enableCreateCurrentUser())
            context = new ScenarioContext(
                currentUser -> userHelper.create(createUserRequestDto(b -> b.id(currentUser.getUserId()))));
        mockDslWebServer = new MockWebServer();
        mockDslWebServer.start(8181);
    }

    @AfterEach
    @SneakyThrows
    void cleanup() {
        mockDslWebServer.shutdown();
    }

    protected boolean enableCreateCurrentUser() {
        return true;
    }

    protected UUID getCurrentUserId() {
        assertNotNull(context);
        return context.getCurrentUser().getUserId();
    }
}
