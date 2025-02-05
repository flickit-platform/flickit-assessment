package org.flickit.assessment.scenario.test.kit.assessmentkit;

import jakarta.persistence.EntityManager;
import okhttp3.mockwebserver.MockResponse;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.CreateKitByDslRequestDto;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.util.FileUtils.createMultipartFile;
import static org.flickit.assessment.scenario.util.FileUtils.readFileToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class CreateKitByDslScenarioTest extends AbstractScenarioTest {

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    EntityManager entityManager;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Test
    void createKitByDsl() {
        final Number expertGroupId = createExpertGroup();
        final Number kitDslId = uploadDsl(expertGroupId);
        final Long kitTagId = createKitTag();

        var request = createKitByDslRequestDto(a -> a
            .expertGroupId(expertGroupId.longValue())
            .kitDslId(kitDslId.longValue())
            .tagIds(List.of(kitTagId))
        );

        int maturityLevelsCountBefore = jpaTemplate.count(MaturityLevelJpaEntity.class);
        int subjectsCountBefore = jpaTemplate.count(SubjectJpaEntity.class);
        int attributesCountBefore = jpaTemplate.count(AttributeJpaEntity.class);
        int questionnairesCountBefore = jpaTemplate.count(QuestionnaireJpaEntity.class);
        int questionsCountBefore = jpaTemplate.count(QuestionJpaEntity.class);

        var response = kitHelper.create(context, request);

        response.then()
            .statusCode(201)
            .body("kitId", notNullValue());

        final Number kitId = response.path("kitId");

        AssessmentKitJpaEntity loadedAssessmentKit = entityManager.createQuery(
                "SELECT k FROM AssessmentKitJpaEntity k JOIN FETCH k.accessGrantedUsers WHERE k.id = :kitId",
                AssessmentKitJpaEntity.class)
            .setParameter("kitId", kitId)
            .getSingleResult();

        assertAssessmentKit(kitId, loadedAssessmentKit, request);

        int maturityLevelsCountAfter = jpaTemplate.count(MaturityLevelJpaEntity.class);
        int subjectsCountAfter = jpaTemplate.count(SubjectJpaEntity.class);
        int attributesCountAfter = jpaTemplate.count(AttributeJpaEntity.class);
        int questionnairesCountAfter = jpaTemplate.count(QuestionnaireJpaEntity.class);
        int questionsCountAfter = jpaTemplate.count(QuestionJpaEntity.class);

        assertEquals(4, maturityLevelsCountAfter - maturityLevelsCountBefore);
        assertEquals(2, subjectsCountAfter - subjectsCountBefore);
        assertEquals(3, attributesCountAfter, attributesCountBefore);
        assertEquals(2, questionnairesCountAfter - questionnairesCountBefore);
        assertEquals(10, questionsCountAfter - questionsCountBefore);
    }

    private void assertAssessmentKit(Number kitId, AssessmentKitJpaEntity loadedAssessmentKit, CreateKitByDslRequestDto request) {
        assertEquals(kitId.longValue(), loadedAssessmentKit.getId());
        assertNotNull(loadedAssessmentKit.getCode());
        assertEquals(request.title(), loadedAssessmentKit.getTitle());
        assertEquals(request.summary(), loadedAssessmentKit.getSummary());
        assertEquals(request.about(), loadedAssessmentKit.getAbout());
        assertFalse(loadedAssessmentKit.getPublished());
        assertEquals(request.isPrivate(), loadedAssessmentKit.getIsPrivate());
        assertEquals(request.expertGroupId(), loadedAssessmentKit.getExpertGroupId());
        assertNotNull(loadedAssessmentKit.getCreationTime());
        assertNotNull(loadedAssessmentKit.getLastModificationTime());
        assertEquals(getCurrentUserId(), loadedAssessmentKit.getCreatedBy());
        assertEquals(getCurrentUserId(), loadedAssessmentKit.getLastModifiedBy());
        assertFalse(loadedAssessmentKit.getAccessGrantedUsers().isEmpty());
        assertEquals(getCurrentUserId(), loadedAssessmentKit.getAccessGrantedUsers().iterator().next().getId());
        assertNotNull(loadedAssessmentKit.getLastMajorModificationTime());
        assertNotNull(loadedAssessmentKit.getKitVersionId());
    }

    private Number createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);

        return response.path("id");
    }

    private Number uploadDsl(Number expertGroupId) {
        MockMultipartFile file = createMultipartFile("dummy-dsl.zip", "dslFile", "application/zip");

        var json = readFileToString("dsl.json");
        mockDslWebServer.enqueue(new MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json"));

        var dslResponse = kitDslHelper.uploadDsl(context, file, expertGroupId.longValue());

        return dslResponse.path("kitDslId");
    }

    private Long createKitTag() {
        KitTagJpaEntity kitTagEntity = new KitTagJpaEntity(1L, "tag-1", "tag 1");
        jpaTemplate.persist(kitTagEntity);
        return kitTagEntity.getId();
    }
}
