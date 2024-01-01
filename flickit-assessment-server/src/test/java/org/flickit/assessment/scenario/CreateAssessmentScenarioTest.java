package org.flickit.assessment.scenario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flickit.assessment.FlickitAssessmentApplication;
import org.flickit.assessment.core.adapter.in.rest.assessment.CreateAssessmentRequestDto;
import org.flickit.assessment.core.adapter.in.rest.assessment.CreateAssessmentResponseDto;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.scenario.helper.persistence.DatabaseTruncator;
import org.flickit.assessment.scenario.helper.persistence.JpaTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.scenario.helper.auth.JwtTokenTestUtils.generateJwtTokenAsAuthHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FlickitAssessmentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentScenarioTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate flickitPlatformRestTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    DatabaseTruncator databaseTruncator;

    @Autowired
    JpaTestTemplate jpaTemplate;


    private MockRestServiceServer mockServer;
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(flickitPlatformRestTemplate);
        databaseTruncator.truncateTables();
    }

    @Test
    void createAssessment() {
        CreateAssessmentRequestDto request = new CreateAssessmentRequestDto(1L, "title", 302L, 2);

        UserJpaEntity user = new UserJpaEntity();
        user.setIsSuperUser(false);
        user.setIsActive(true);
        user.setIsStaff(false);
        user.setDisplayName("flickit test");
        user.setEmail("flickit_test@gmail.com");
        user.setPassword("12345");
        jpaTemplate.persist(user);

        headers.set(AUTHORIZATION, generateJwtTokenAsAuthHeader(user.getId()));

        ResponseEntity<CreateAssessmentResponseDto> response = restTemplate.exchange(
            createURLWithPort("/assessment-core/api/assessments"),
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            CreateAssessmentResponseDto.class);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());

        final UUID assessmentId = response.getBody().id();
        AssessmentJpaEntity savedEntity = jpaTemplate.load(assessmentId, AssessmentJpaEntity.class);
        assertEquals(request.spaceId(), savedEntity.getSpaceId());
        assertEquals(request.assessmentKitId(), savedEntity.getAssessmentKitId());
        assertEquals(request.colorId(), savedEntity.getColorId());
        // TODO

        List<AssessmentResultJpaEntity> assessmentResults = jpaTemplate.search(AssessmentResultJpaEntity.class, (r, cq, cb) ->
            cb.equal(r.get("assessment").get("id"), assessmentId)
        );
        assertEquals(1, assessmentResults.size());
        //TODO

        final UUID assessmentResultId = assessmentResults.get(0).getId();

        List<SubjectValueJpaEntity> subjectValues = jpaTemplate.search(SubjectValueJpaEntity.class, (r, cq, cb) ->
            cb.equal(r.get("assessmentResult").get("id"), assessmentResultId)
        );
        assertEquals(2, subjectValues.size());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
