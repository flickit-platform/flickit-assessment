package org.flickit.assessment.scenario.test.kit.assessmentkit;

import lombok.SneakyThrows;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaEntity;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaEntity;
import org.flickit.assessment.kit.adapter.in.rest.assessmentkit.CreateKitByDslRequestDto;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.scenario.test.AbstractScenarioTest;
import org.flickit.assessment.scenario.test.kit.kitdsl.KitDslTestHelper;
import org.flickit.assessment.scenario.test.kit.tag.KitTagTestHelper;
import org.flickit.assessment.scenario.test.users.expertgroup.ExpertGroupTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.scenario.fixture.request.CreateExpertGroupRequestDtoMother.createExpertGroupRequestDto;
import static org.flickit.assessment.scenario.fixture.request.CreateKitByDslRequestDtoMother.createKitByDslRequestDto;
import static org.flickit.assessment.scenario.util.FileUtils.readFileToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class CreateKitByDslScenarioTest extends AbstractScenarioTest {

    @Autowired
    KitTestHelper kitHelper;

    @Autowired
    ExpertGroupTestHelper expertGroupHelper;

    @Autowired
    KitDslTestHelper kitDslHelper;

    @Autowired
    KitTagTestHelper kitTagHelper;

    int maturityLevelsCountBefore;
    int subjectsCountBefore;
    int attributesCountBefore;
    int questionnairesCountBefore;
    int questionsCountBefore;
    int answerRangesCountBefore;

    @BeforeEach
    void before() {
        maturityLevelsCountBefore = jpaTemplate.count(MaturityLevelJpaEntity.class);
        subjectsCountBefore = jpaTemplate.count(SubjectJpaEntity.class);
        attributesCountBefore = jpaTemplate.count(AttributeJpaEntity.class);
        questionnairesCountBefore = jpaTemplate.count(QuestionnaireJpaEntity.class);
        questionsCountBefore = jpaTemplate.count(QuestionJpaEntity.class);
        answerRangesCountBefore = jpaTemplate.count(AnswerRangeJpaEntity.class);
    }

    @Test
    void createKitByDsl() {
        final Long expertGroupId = createExpertGroup();
        final Long kitDslId = uploadDsl(expertGroupId);
        final Long kitTagId = kitTagHelper.createKitTag();
        final AssessmentKitDslModel kitDslModel = readDslJson();

        var request = createKitByDslRequestDto(a -> a
                .expertGroupId(expertGroupId)
                .kitDslId(kitDslId)
                .tagIds(List.of(kitTagId))
        );

        var response = kitHelper.create(context, request);

        response.then()
                .statusCode(201)
                .body("kitId", notNullValue());
        final Number kitId = response.path("kitId");

        AssessmentKitJpaEntity loadedAssessmentKit = jpaTemplate.load(kitId, AssessmentKitJpaEntity.class);

        assertAssessmentKit(kitId.longValue(), loadedAssessmentKit, request);
        assertKitTagRelation(kitId.longValue(), request);
        Long kitVersionId = loadedAssessmentKit.getKitVersionId();

        assertMaturityLevels(kitDslModel.getMaturityLevels(), kitVersionId);
        assertSubjects(kitDslModel, kitVersionId);

        var answerRanges = loadByKitVersionId(AnswerRangeJpaEntity.class, kitVersionId);
        var answerOptions = loadByKitVersionId(AnswerOptionJpaEntity.class, kitVersionId);
        var rangeIdToOptions = answerOptions.stream()
                .collect(groupingBy(AnswerOptionJpaEntity::getAnswerRangeId));

        assertAnswerRanges(kitDslModel, answerRanges, rangeIdToOptions);
        assertQuestionnaires(kitDslModel, kitVersionId, answerRanges, rangeIdToOptions);
        assertSubjectQuestionnaires(kitVersionId);
    }

    private Long createExpertGroup() {
        var request = createExpertGroupRequestDto();
        var response = expertGroupHelper.create(context, request);
        Number id = response.path("id");
        return id.longValue();
    }

    private Long uploadDsl(Long expertGroupId) {
        var response = kitDslHelper.uploadDsl(context, "dummy-dsl.zip", "dsl.json", expertGroupId);
        Number id = response.path("kitDslId");
        return id.longValue();
    }

    @SneakyThrows
    private AssessmentKitDslModel readDslJson() {
        var dslJsonContent = readFileToString("dsl.json");
        return objectMapper.readValue(dslJsonContent, AssessmentKitDslModel.class);
    }

    private void assertAssessmentKit(Long kitId, AssessmentKitJpaEntity loadedAssessmentKit, CreateKitByDslRequestDto request) {
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
        assertNotNull(loadedAssessmentKit.getLastMajorModificationTime());
        assertNotNull(loadedAssessmentKit.getKitVersionId());

        var kitUsers = loadKitUserAccesses(kitId);
        assertEquals(1, kitUsers.size());
        assertEquals(getCurrentUserId(), kitUsers.getFirst().getUserId());
    }

    private void assertKitTagRelation(Long kitId, CreateKitByDslRequestDto request) {
        var entities = jpaTemplate.search(KitTagRelationJpaEntity.class, (root, query, cb) ->
                cb.equal(root.get(KitTagRelationJpaEntity.Fields.kitId), kitId)
        );
        assertThat(entities)
                .map(KitTagRelationJpaEntity::getTagId)
                .containsExactlyInAnyOrderElementsOf(request.tagIds());
    }

    private void assertMaturityLevels(List<MaturityLevelDslModel> dslMaturityLevels, Long kitVersionId) {
        int maturityLevelsCountAfter = jpaTemplate.count(MaturityLevelJpaEntity.class);
        assertEquals(dslMaturityLevels.size(), maturityLevelsCountAfter - maturityLevelsCountBefore);

        dslMaturityLevels = dslMaturityLevels.stream()
                .sorted(comparingInt(MaturityLevelDslModel::getIndex))
                .toList();
        var loadedMaturityLevels = loadByKitVersionId(MaturityLevelJpaEntity.class, kitVersionId).stream()
                .sorted(comparingInt(MaturityLevelJpaEntity::getIndex))
                .toList();
        Map<Long, String> maturityIdToCode = loadedMaturityLevels.stream()
                .collect(toMap(MaturityLevelJpaEntity::getId, MaturityLevelJpaEntity::getCode));

        assertThat(loadedMaturityLevels)
                .zipSatisfy(dslMaturityLevels, (entity, model) -> {
                    assertEquals(model.getCode(), entity.getCode());
                    assertEquals(model.getDescription(), entity.getDescription());
                    assertEquals(model.getIndex(), entity.getIndex());
                    assertEquals(model.getTitle(), entity.getTitle());
                    assertEquals(model.getValue(), entity.getValue());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

                    Map<String, Integer> maturityCodeToCompetenceValue = model.getCompetencesCodeToValueMap();
                    var loadedCompetences = loadMaturityCompetences(kitVersionId, entity.getId());
                    if (isEmpty(maturityCodeToCompetenceValue)) {
                        assertThat(loadedCompetences).isEmpty();
                    } else {
                        assertThat(loadedCompetences)
                                .hasSize(maturityCodeToCompetenceValue.size())
                                .allSatisfy(c -> {
                                    var maturityCode = maturityIdToCode.get(c.getEffectiveLevelId());
                                    var competenceValue = maturityCodeToCompetenceValue.get(maturityCode);
                                    assertEquals(competenceValue, c.getValue());

                                    assertNotNull(c.getCreationTime());
                                    assertNotNull(c.getLastModificationTime());
                                    assertEquals(getCurrentUserId(), c.getCreatedBy());
                                    assertEquals(getCurrentUserId(), c.getLastModifiedBy());
                                });
                    }
                });
    }

    private void assertSubjects(AssessmentKitDslModel kitDslModel, Long kitVersionId) {
        int subjectsCountAfter = jpaTemplate.count(SubjectJpaEntity.class);
        assertEquals(kitDslModel.getSubjects().size(), subjectsCountAfter - subjectsCountBefore);

        int attributesCountAfter = jpaTemplate.count(AttributeJpaEntity.class);
        assertEquals(kitDslModel.getAttributes().size(), attributesCountAfter - attributesCountBefore);

        var dslSubjects = kitDslModel.getSubjects().stream()
                .sorted(comparingInt(SubjectDslModel::getIndex))
                .toList();
        var loadedSubjects = loadByKitVersionId(SubjectJpaEntity.class, kitVersionId).stream()
                .sorted(comparingInt(SubjectJpaEntity::getIndex))
                .toList();

        var subjectCodeToAttributeModels = kitDslModel.getAttributes().stream()
                .collect(groupingBy(AttributeDslModel::getSubjectCode));

        assertThat(loadedSubjects)
                .zipSatisfy(dslSubjects, (entity, model) -> {
                    assertEquals(model.getCode(), entity.getCode());
                    assertEquals(model.getDescription(), entity.getDescription());
                    assertEquals(model.getIndex(), entity.getIndex());
                    assertEquals(model.getTitle(), entity.getTitle());
                    assertEquals(model.getWeight(), entity.getWeight());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

                    assertAttributes(subjectCodeToAttributeModels.get(model.getCode()), kitVersionId, entity.getId());
                });
    }

    private void assertAttributes(List<AttributeDslModel> dslAttributes, Long kitVersionId, Long subjectId) {
        var loadedAttributes = loadSubjectAttributes(kitVersionId, subjectId).stream()
                .sorted(comparingInt(AttributeJpaEntity::getIndex))
                .toList();

        dslAttributes = dslAttributes.stream()
                .sorted(comparingInt(AttributeDslModel::getIndex))
                .toList();

        assertThat(loadedAttributes)
                .zipSatisfy(dslAttributes, (entity, model) -> {
                    assertEquals(model.getCode(), entity.getCode());
                    assertEquals(model.getDescription(), entity.getDescription());
                    assertEquals(model.getIndex(), entity.getIndex());
                    assertEquals(model.getTitle(), entity.getTitle());
                    assertEquals(model.getWeight(), entity.getWeight());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());
                });
    }

    private void assertAnswerRanges(AssessmentKitDslModel kitDslModel,
                                    List<AnswerRangeJpaEntity> loadedAnswerRanges,
                                    Map<Long, List<AnswerOptionJpaEntity>> rangeIdToOptions) {
        var dslRanges = kitDslModel.getAnswerRanges().stream()
                .sorted(comparing(AnswerRangeDslModel::getCode))
                .toList();
        var loadedReusableRanges = loadedAnswerRanges.stream()
                .filter(AnswerRangeJpaEntity::isReusable)
                .sorted(comparing(AnswerRangeJpaEntity::getCode))
                .toList();

        assertThat(loadedReusableRanges)
                .zipSatisfy(dslRanges, (entity, model) -> {
                    assertEquals(model.getCode(), entity.getCode());
                    assertEquals(model.getTitle(), entity.getTitle());
                    assertTrue(entity.isReusable());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

                    var dslOptions = model.getAnswerOptions().stream()
                            .sorted(comparingInt(AnswerOptionDslModel::getIndex))
                            .toList();

                    var loadedOptions = rangeIdToOptions.get(entity.getId()).stream()
                            .sorted(comparingInt(AnswerOptionJpaEntity::getIndex))
                            .toList();
                    assertAnswerOptions(dslOptions, loadedOptions);

                });
    }

    private void assertQuestionnaires(AssessmentKitDslModel kitDslModel,
                                      Long kitVersionId,
                                      List<AnswerRangeJpaEntity> answerRanges,
                                      Map<Long, List<AnswerOptionJpaEntity>> rangeIdToOptions) {
        int questionnairesCountAfter = jpaTemplate.count(QuestionnaireJpaEntity.class);
        assertEquals(kitDslModel.getQuestionnaires().size(), questionnairesCountAfter - questionnairesCountBefore);

        int questionsCountAfter = jpaTemplate.count(QuestionJpaEntity.class);
        assertEquals(kitDslModel.getQuestions().size(), questionsCountAfter - questionsCountBefore);

        var dslQuestionnaires = kitDslModel.getQuestionnaires().stream()
                .sorted(comparingInt(QuestionnaireDslModel::getIndex))
                .toList();
        var loadedQuestionnaires = loadByKitVersionId(QuestionnaireJpaEntity.class, kitVersionId).stream()
                .sorted(comparingInt(QuestionnaireJpaEntity::getIndex))
                .toList();

        var questionnaireCodeToQuestionModels = kitDslModel.getQuestions().stream()
                .collect(groupingBy(QuestionDslModel::getQuestionnaireCode));

        var questionIdToImpacts = loadByKitVersionId(QuestionImpactJpaEntity.class, kitVersionId).stream()
                .collect(groupingBy(QuestionImpactJpaEntity::getQuestionId));

        var codeToAttribute = loadByKitVersionId(AttributeJpaEntity.class, kitVersionId).stream()
                .collect(toMap(AttributeJpaEntity::getCode, Function.identity()));

        var maturityLevelIdToCode = loadByKitVersionId(MaturityLevelJpaEntity.class, kitVersionId).stream()
                .collect(toMap(MaturityLevelJpaEntity::getId, MaturityLevelJpaEntity::getCode));

        assertThat(loadedQuestionnaires)
                .zipSatisfy(dslQuestionnaires, (entity, model) -> {
                    assertEquals(model.getCode(), entity.getCode());
                    assertEquals(model.getDescription(), entity.getDescription());
                    assertEquals(model.getIndex(), entity.getIndex());
                    assertEquals(model.getTitle(), entity.getTitle());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

                    assertQuestions(questionnaireCodeToQuestionModels.get(model.getCode()),
                            kitVersionId,
                            entity.getId(),
                            questionIdToImpacts,
                            codeToAttribute,
                            maturityLevelIdToCode,
                            answerRanges,
                            rangeIdToOptions);
                });
    }

    private void assertQuestions(List<QuestionDslModel> dslQuestions,
                                 Long kitVersionId,
                                 Long questionnaireId,
                                 Map<Long, List<QuestionImpactJpaEntity>> questionToImpacts,
                                 Map<String, AttributeJpaEntity> codeToAttribute,
                                 Map<Long, String> maturityLevelIdToCode,
                                 List<AnswerRangeJpaEntity> answerRanges,
                                 Map<Long, List<AnswerOptionJpaEntity>> rangeIdToOptions) {
        var loadedQuestions = loadQuestionnaireQuestions(kitVersionId, questionnaireId).stream()
                .sorted(comparingInt(QuestionJpaEntity::getIndex))
                .toList();

        dslQuestions = dslQuestions.stream()
                .sorted(comparingInt(QuestionDslModel::getIndex))
                .toList();

        assertThat(loadedQuestions)
                .zipSatisfy(dslQuestions, (entity, model) -> {
                    assertEquals(model.getCode(), entity.getCode());
                    assertEquals(model.getDescription(), entity.getHint());
                    assertEquals(model.getIndex(), entity.getIndex());
                    assertEquals(model.getTitle(), entity.getTitle());
                    assertEquals(model.isMayNotBeApplicable(), entity.getMayNotBeApplicable());
                    assertEquals(model.isAdvisable(), entity.getAdvisable());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

                    var impacts = questionToImpacts.get(entity.getId());
                    assertQuestionImpacts(model.getQuestionImpacts(), impacts, codeToAttribute, maturityLevelIdToCode);

                    var idToRange = answerRanges.stream()
                            .collect(Collectors.toMap(AnswerRangeJpaEntity::getId, Function.identity()));

                    if (model.getAnswerRangeCode() != null)
                        assertEquals(model.getAnswerRangeCode(), idToRange.get(entity.getAnswerRangeId()).getCode());
                    else {
                        assertFalse(idToRange.get(entity.getAnswerRangeId()).isReusable());
                        var dslOptions = model.getAnswerOptions().stream()
                                .sorted(comparingInt(AnswerOptionDslModel::getIndex))
                                .toList();
                        var loadedOptions = rangeIdToOptions.get(entity.getAnswerRangeId()).stream()
                                .sorted(comparingInt(AnswerOptionJpaEntity::getIndex))
                                .toList();
                        assertAnswerOptions(dslOptions, loadedOptions);
                    }
                });
    }

    private void assertQuestionImpacts(List<QuestionImpactDslModel> dslImpacts, List<QuestionImpactJpaEntity> loadedImpacts,
                                       Map<String, AttributeJpaEntity> codeToAttribute,
                                       Map<Long, String> maturityLevelIdToCode) {
        var attributeIdToImpact = loadedImpacts.stream()
                .collect(toMap(QuestionImpactJpaEntity::getAttributeId, Function.identity()));

        assertEquals(dslImpacts.size(), loadedImpacts.size());

        dslImpacts.forEach(model -> {
            var attribute = codeToAttribute.get(model.getAttributeCode());
            var entity = attributeIdToImpact.get(attribute.getId());

            assertNotNull(entity);

            assertEquals(model.getAttributeCode(), attribute.getCode());
            assertEquals(model.getMaturityLevel().getCode(), maturityLevelIdToCode.get(entity.getMaturityLevelId()));
            assertEquals(model.getWeight(), entity.getWeight());

            assertNotNull(entity.getCreationTime());
            assertNotNull(entity.getLastModificationTime());
            assertEquals(getCurrentUserId(), entity.getCreatedBy());
            assertEquals(getCurrentUserId(), entity.getLastModifiedBy());
        });
    }

    private void assertAnswerOptions(List<AnswerOptionDslModel> dslOptions, List<AnswerOptionJpaEntity> loadedOptions) {
        assertThat(loadedOptions)
                .zipSatisfy(dslOptions, (entity, model) -> {
                    assertEquals(model.getIndex(), entity.getIndex());
                    assertEquals(model.getCaption(), entity.getTitle());
                    assertEquals(model.getValue(), entity.getValue());

                    assertNotNull(entity.getCreationTime());
                    assertNotNull(entity.getLastModificationTime());
                    assertEquals(getCurrentUserId(), entity.getCreatedBy());
                    assertEquals(getCurrentUserId(), entity.getLastModifiedBy());

                });
    }

    private void assertSubjectQuestionnaires(Long kitVersionId) {
        var questionnaireCodeToId = loadByKitVersionId(QuestionnaireJpaEntity.class, kitVersionId).stream()
                .collect(Collectors.toMap(QuestionnaireJpaEntity::getCode, QuestionnaireJpaEntity::getId));

        var subjectCodeToId = loadByKitVersionId(SubjectJpaEntity.class, kitVersionId).stream()
                .collect(Collectors.toMap(SubjectJpaEntity::getCode, SubjectJpaEntity::getId));

        var subjectQuestionnaires = loadByKitVersionId(SubjectQuestionnaireJpaEntity.class, kitVersionId);

        var subjectQuestionnairePairs = List.of(
                Pair.of("Team", "Development"),
                Pair.of("Team", "TeamCollaboration"),
                Pair.of("Software", "Development"),
                Pair.of("Software", "TeamCollaboration")
        );

        assertEquals(subjectQuestionnairePairs.size(), subjectQuestionnaires.size());

        subjectQuestionnairePairs.forEach(a -> {
            var subjectId = subjectCodeToId.get(a.getKey());
            var questionnaireId = questionnaireCodeToId.get(a.getValue());
            assertTrue(subjectQuestionnaires.stream()
                    .anyMatch(sq -> subjectId.equals(sq.getSubjectId()) && questionnaireId.equals(sq.getQuestionnaireId())));
        });
    }

    private List<KitUserAccessJpaEntity> loadKitUserAccesses(Long kitId) {
        return jpaTemplate.search(KitUserAccessJpaEntity.class,
                (root, query, cb) -> cb.equal(root.get(KitUserAccessJpaEntity.Fields.kitId), kitId));
    }

    private List<LevelCompetenceJpaEntity> loadMaturityCompetences(Long kitVersionId, Long affectedLevelId) {
        return jpaTemplate.search(LevelCompetenceJpaEntity.class,
                (root, query, cb) ->
                        cb.and(
                                cb.equal(root.get(LevelCompetenceJpaEntity.Fields.kitVersionId), kitVersionId),
                                cb.equal(root.get(LevelCompetenceJpaEntity.Fields.affectedLevelId), affectedLevelId)
                        ));
    }

    private List<AttributeJpaEntity> loadSubjectAttributes(Long kitVersionId, Long subjectId) {
        return jpaTemplate.search(AttributeJpaEntity.class,
                (root, query, cb) ->
                        cb.and(
                                cb.equal(root.get(AttributeJpaEntity.Fields.kitVersionId), kitVersionId),
                                cb.equal(root.get(AttributeJpaEntity.Fields.subjectId), subjectId)
                        ));
    }

    private List<QuestionJpaEntity> loadQuestionnaireQuestions(Long kitVersionId, Long questionnaireId) {
        return jpaTemplate.search(QuestionJpaEntity.class,
                (root, query, cb) ->
                        cb.and(
                                cb.equal(root.get(QuestionJpaEntity.Fields.kitVersionId), kitVersionId),
                                cb.equal(root.get(QuestionJpaEntity.Fields.questionnaireId), questionnaireId)
                        ));
    }

    private <T> List<T> loadByKitVersionId(Class<T> clazz, Long kitVersionId) {
        return jpaTemplate.search(clazz,
                (root, query, cb) -> cb.equal(root.get("kitVersionId"), kitVersionId));
    }
}
