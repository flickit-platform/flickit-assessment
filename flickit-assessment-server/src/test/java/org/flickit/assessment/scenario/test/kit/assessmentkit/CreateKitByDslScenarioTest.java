package org.flickit.assessment.scenario.test.kit.assessmentkit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import okhttp3.mockwebserver.MockResponse;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
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

import java.util.ArrayList;
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
        Long kitVersionId = loadedAssessmentKit.getKitVersionId();

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

        var lastMaturityLevel = loadEntityByCode(MaturityLevelJpaEntity.class, kitVersionId, "StateOfTheArt");
        assertMaturityLevel(lastMaturityLevel, kitVersionId);
        var teamSubject = loadEntityByCode(SubjectJpaEntity.class, kitVersionId, "Team");
        assertSubject(teamSubject, kitVersionId);
        var agileWorkflowAttribute = loadEntityByCode(AttributeJpaEntity.class, kitVersionId, "AgileWorkflow");
        assertAttribute(agileWorkflowAttribute, kitVersionId, teamSubject.getId());
        var developmentQuestionnaire = loadEntityByCode(QuestionnaireJpaEntity.class, kitVersionId, "Development");
        assertQuestionnaire(developmentQuestionnaire, kitVersionId);
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

    private <T> T loadEntityByCode(Class<T> clazz, Long kitVersionId, String code) {
        return jpaTemplate.findSingle(clazz,
            (root, query, cb) -> {
                var predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("kitVersionId"), kitVersionId));
                predicates.add(cb.equal(root.get("code"), code));
                return query
                    .where(cb.and(predicates.toArray(new Predicate[0])))
                    .getRestriction();
            });
    }

    private void assertMaturityLevel(MaturityLevelJpaEntity entity, Long kitVersionId) {
        assertNotNull(entity.getId());
        assertEquals(kitVersionId, entity.getKitVersionId());
        assertEquals("StateOfTheArt", entity.getCode());
        assertEquals(4, entity.getIndex());
        assertEquals("State of the Art", entity.getTitle());
        assertEquals("Cutting-edge tools lead to exceptional software quality and peak team performance, " +
                "supporting continuous improvement and innovation.",
            entity.getDescription());
        assertEquals(4, entity.getValue());
        assertNotNull(entity.getCreationTime());
        assertNotNull(entity.getLastModificationTime());
        assertEquals(getCurrentUserId(), entity.getCreatedBy());
        assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

        assertCompetences(kitVersionId, entity.getId());
    }

    private void assertCompetences(Long kitVersionId, Long stateOfTheArtLevelId) {
        var competencesCount = jpaTemplate.count(LevelCompetenceJpaEntity.class,
            (root, query, cb) -> {
                var predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(LevelCompetenceJpaEntity.Fields.kitVersionId), kitVersionId));
                predicates.add(cb.equal(root.get(LevelCompetenceJpaEntity.Fields.affectedLevelId), stateOfTheArtLevelId));
                return query
                    .where(cb.and(predicates.toArray(new Predicate[0])))
                    .getRestriction();
            });
        assertEquals(3, competencesCount);

        var competence = jpaTemplate.findSingle(LevelCompetenceJpaEntity.class,
            (root, query, cb) -> {
                var predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(LevelCompetenceJpaEntity.Fields.kitVersionId), kitVersionId));
                predicates.add(cb.equal(root.get(LevelCompetenceJpaEntity.Fields.affectedLevelId), stateOfTheArtLevelId));
                predicates.add(cb.equal(root.get(LevelCompetenceJpaEntity.Fields.effectiveLevelId), stateOfTheArtLevelId));
                return query
                    .where(cb.and(predicates.toArray(new Predicate[0])))
                    .getRestriction();
            });
        assertNotNull(competence.getId());
        assertEquals(kitVersionId, competence.getKitVersionId());
        assertEquals(stateOfTheArtLevelId, competence.getAffectedLevelId());
        assertEquals(stateOfTheArtLevelId, competence.getEffectiveLevelId());
        assertEquals(40, competence.getValue());
        assertNotNull(competence.getCreationTime());
        assertNotNull(competence.getLastModificationTime());
        assertEquals(getCurrentUserId(), competence.getCreatedBy());
        assertEquals(getCurrentUserId(), competence.getLastModifiedBy());
    }

    private void assertSubject(SubjectJpaEntity entity, Long kitVersionId) {
        assertNotNull(entity.getId());
        assertEquals(kitVersionId, entity.getKitVersionId());
        assertEquals("Team", entity.getCode());
        assertEquals(1, entity.getIndex());
        assertEquals("Team", entity.getTitle());
        assertEquals("How have the tools employed contributed to maintaining a well-being and high-performing team?",
            entity.getDescription());
        assertEquals(1, entity.getWeight());
        assertNotNull(entity.getCreationTime());
        assertNotNull(entity.getLastModificationTime());
        assertEquals(getCurrentUserId(), entity.getCreatedBy());
        assertEquals(getCurrentUserId(), entity.getLastModifiedBy());
    }

    private void assertAttribute(AttributeJpaEntity entity, Long kitVersionId, Long subjectId) {
        assertNotNull(entity.getId());
        assertEquals(kitVersionId, entity.getKitVersionId());
        assertEquals("AgileWorkflow", entity.getCode());
        assertEquals(1, entity.getIndex());
        assertEquals("Team Agility", entity.getTitle());
        assertEquals("How is the team equipped to continuously and incrementally meet the growing needs of customers in a rapid manner?",
            entity.getDescription());
        assertEquals(1, entity.getWeight());
        assertNotNull(entity.getCreationTime());
        assertNotNull(entity.getLastModificationTime());
        assertEquals(getCurrentUserId(), entity.getCreatedBy());
        assertEquals(getCurrentUserId(), entity.getLastModifiedBy());
        assertEquals(subjectId, entity.getSubjectId());
    }

    private void assertQuestionnaire(QuestionnaireJpaEntity entity, Long kitVersionId) {
        assertNotNull(entity.getId());
        assertEquals(kitVersionId, entity.getKitVersionId());
        assertEquals("Development", entity.getCode());
        assertEquals(1, entity.getIndex());
        assertEquals("Development", entity.getTitle());
        assertEquals("This category includes tools ", entity.getDescription());
        assertNotNull(entity.getCreationTime());
        assertNotNull(entity.getLastModificationTime());
        assertEquals(getCurrentUserId(), entity.getCreatedBy());
        assertEquals(getCurrentUserId(), entity.getLastModifiedBy());
    }
}
