package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_ANSWER_RANGES;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_MATURITY_LEVELS;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_ATTRIBUTES;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.createAttribute;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.dsl.AnswerOptionDslModelMother.answerOptionDslModel;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother.questionDslModel;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionImpactDslModelMother.questionImpactDslModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionCreateKitPersisterTest {

    @InjectMocks
    private QuestionCreateKitPersister persister;
    @Mock
    private CreateQuestionPort createQuestionPort;
    @Mock
    private CreateQuestionImpactPort createQuestionImpactPort;
    @Mock
    private CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;
    @Mock
    private CreateAnswerRangePort createAnswerRangePort;
    @Mock
    private LoadAnswerOptionsPort loadAnswerOptionsPort;

    @Captor
    private ArgumentCaptor<CreateQuestionPort.Param> createQuestionParamCaptor;

    @Captor
    private ArgumentCaptor<QuestionImpact> questionImpactCaptor;

    @Captor
    private ArgumentCaptor<CreateAnswerOptionImpactPort.Param> createAnswerOptionImpactParamCaptor;

    @Test
    void testOrder() {
        Assertions.assertEquals(6, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveQuestionAndItsRelatedEntities() {
        long kitVersionId = 1L;
        long answerRangeId = 1L;
        UUID currentUserId = UUID.randomUUID();

        var levelTwo = levelTwo();
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var question = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, Boolean.FALSE, Boolean.TRUE, 153L, questionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var impact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, question.getId());
        var answerOption1 = createAnswerOption(question.getAnswerRangeId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(question.getAnswerRangeId(), OPTION_TITLE, OPTION_INDEX2);
        var optionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var optionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        impact.setOptionImpacts(List.of(optionImpact1, optionImpact2));
        question.setOptions(List.of(answerOption1, answerOption2));
        question.setImpacts(List.of(impact));
        questionnaire.setQuestions(List.of(question));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1,
            List.of(dslImpact), dslAnswerOptionList, null, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .questions(List.of(dslQuestion))
            .build();

        var createAnswerRangeParam = new CreateAnswerRangePort.Param(kitVersionId, null, null, false, currentUserId);

        when(createAnswerRangePort.persist(createAnswerRangeParam)).thenReturn(answerRangeId);
        when(createQuestionPort.persist(any())).thenReturn(question.getId());
        var createOption1Param = new CreateAnswerOptionPort.Param(answerOption1.getTitle(), answerOption1.getIndex(), answerRangeId, OPTION_VALUE1, kitVersionId, currentUserId);
        var createOption2Param = new CreateAnswerOptionPort.Param(answerOption2.getTitle(), answerOption2.getIndex(), answerRangeId, OPTION_VALUE2, kitVersionId, currentUserId);
        when(createAnswerOptionPort.persist(createOption1Param)).thenReturn(answerOption1.getId());
        when(createAnswerOptionPort.persist(createOption2Param)).thenReturn(answerOption2.getId());

        when(createQuestionImpactPort.persist(any())).thenReturn(impact.getId());

        when(createAnswerOptionImpactPort.persist(any())).thenReturn(optionImpact1.getId());
        when(createAnswerOptionImpactPort.persist(any())).thenReturn(optionImpact2.getId());

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_QUESTIONNAIRES, Map.of(questionnaire.getCode(), questionnaire.getId()));
        context.put(KEY_ATTRIBUTES, Map.of(attribute.getCode(), attribute.getId()));
        context.put(KEY_MATURITY_LEVELS, Map.of(levelTwo.getCode(), levelTwo.getId()));

        persister.persist(context, dslModel, kitVersionId, currentUserId);

        verify(createQuestionPort).persist(createQuestionParamCaptor.capture());
        CreateQuestionPort.Param createQuestionPortParam = createQuestionParamCaptor.getValue();
        assertCreateQuestionPortParam(createQuestionPortParam, dslQuestion, kitVersionId, questionnaire.getId(), answerRangeId, currentUserId);

        verify(createQuestionImpactPort).persist(questionImpactCaptor.capture());
        QuestionImpact questionImpactCaptorValue = questionImpactCaptor.getValue();
        assertCreateQuestionImpactPortParam(questionImpactCaptorValue, attribute.getId(), levelTwo.getId(),
            dslImpact.getWeight(), kitVersionId, question.getId(), currentUserId);

        verify(createAnswerOptionImpactPort, times(2)).persist(createAnswerOptionImpactParamCaptor.capture());
        List<CreateAnswerOptionImpactPort.Param> allValues = createAnswerOptionImpactParamCaptor.getAllValues();
        assertCreateAnswerOptionPortParam(allValues, impact, kitVersionId, currentUserId);
    }

    @Test
    void testPersist_WhenQuestionDslHasAnswerRange_ThenSaveQuestion() {
        long kitVersionId = 1;
        UUID currentUserId = UUID.randomUUID();

        var questionnaire = QuestionnaireMother.questionnaireWithTitle("devops");
        var answerRange = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions(1);
        var question = QuestionMother.createQuestion(answerRange.getId(), questionnaire.getId());
        var attribute = AttributeMother.attributeWithTitle("flexibility");
        var maturityLevel = MaturityLevelMother.levelOne();
        var impact = createQuestionImpact(attribute.getId(), maturityLevel.getId(), 1, question.getId());
        var answerOption1 = answerRange.getAnswerOptions().getFirst();
        var answerOption2 = answerRange.getAnswerOptions().getLast();
        var aoi1 = createAnswerOptionImpact(answerOption1.getId(), answerOption1.getValue());
        var aoi2 = createAnswerOptionImpact(answerOption2.getId(), answerOption2.getValue());
        impact.setOptionImpacts(List.of(aoi1, aoi2));
        question.setImpacts(List.of(impact));
        question.setOptions(null);

        MaturityLevelDslModel maturityLevelDslModel = MaturityLevelDslModelMother.domainToDslModel(maturityLevel);
        Map<Integer, Double> optionIndexToValue = new HashMap<>();
        optionIndexToValue.put(answerOption1.getIndex(), answerOption1.getValue());
        optionIndexToValue.put(answerOption2.getIndex(), answerOption2.getValue());
        QuestionImpactDslModel dslImpact = questionImpactDslModel(attribute.getCode(),
            maturityLevelDslModel,
            null,
            optionIndexToValue,
            impact.getWeight());

        QuestionDslModel questionDslModel = QuestionDslModelMother.domainToDslModel(question,
            b -> b.questionImpacts(List.of(dslImpact))
                .answerRangeCode(answerRange.getCode())
                .questionnaireCode(questionnaire.getCode()));

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_QUESTIONNAIRES, Map.of(questionnaire.getCode(), questionnaire.getId()));
        context.put(KEY_ATTRIBUTES, Map.of(attribute.getCode(), attribute.getId()));
        context.put(KEY_MATURITY_LEVELS, Map.of(maturityLevel.getCode(), maturityLevel.getId()));
        context.put(KEY_ANSWER_RANGES, Map.of(answerRange.getCode(), answerRange.getId()));

        AssessmentKitDslModel kitDslModel = AssessmentKitDslModel.builder()
            .questions(List.of(questionDslModel))
            .build();

        when(createQuestionPort.persist(any(CreateQuestionPort.Param.class))).thenReturn(question.getId());
        when(loadAnswerOptionsPort.loadByRangeId(answerRange.getId(), kitVersionId))
            .thenReturn(List.of(answerOption1, answerOption2));
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(impact.getId());
        when(createAnswerOptionImpactPort.persist(any(CreateAnswerOptionImpactPort.Param.class))).thenReturn(anyLong());

        persister.persist(context, kitDslModel, kitVersionId, currentUserId);

        verifyNoInteractions(createAnswerRangePort, createAnswerOptionPort);

        verify(createQuestionPort).persist(createQuestionParamCaptor.capture());
        CreateQuestionPort.Param createQuestionParamCaptorValue = createQuestionParamCaptor.getValue();
        assertCreateQuestionPortParam(createQuestionParamCaptorValue, questionDslModel, kitVersionId, questionnaire.getId(), answerRange.getId(), currentUserId);

        verify(createQuestionImpactPort).persist(questionImpactCaptor.capture());
        QuestionImpact questionImpactCaptorValue = questionImpactCaptor.getValue();
        assertCreateQuestionImpactPortParam(questionImpactCaptorValue, attribute.getId(), maturityLevel.getId(), dslImpact.getWeight(), kitVersionId, question.getId(), currentUserId);

        verify(createAnswerOptionImpactPort, times(2)).persist(createAnswerOptionImpactParamCaptor.capture());
        List<CreateAnswerOptionImpactPort.Param> captorValues = createAnswerOptionImpactParamCaptor.getAllValues();
        assertCreateAnswerOptionPortParam(captorValues, impact, kitVersionId, currentUserId);
    }

    private void assertCreateQuestionPortParam(CreateQuestionPort.Param captorValue, QuestionDslModel dslQuestion,
                                               long kitVersionId, long questionnaireId, long answerRangeId, UUID currentUserId) {
        assertEquals(dslQuestion.getCode(), captorValue.code());
        assertEquals(dslQuestion.getTitle(), captorValue.title());
        assertEquals(dslQuestion.getIndex(), captorValue.index());
        assertEquals(dslQuestion.getDescription(), captorValue.hint());
        assertEquals(dslQuestion.isMayNotBeApplicable(), captorValue.mayNotBeApplicable());
        assertEquals(dslQuestion.isAdvisable(), captorValue.advisable());
        assertEquals(kitVersionId, captorValue.kitVersionId());
        assertEquals(questionnaireId, captorValue.questionnaireId());
        assertEquals(answerRangeId, captorValue.answerRangeId());
        assertEquals(currentUserId, captorValue.createdBy());
    }

    private void assertCreateQuestionImpactPortParam(QuestionImpact captorValue, long attributeId, long maturityLevelId,
                                                     int dslImpactWeight, long kitVersionId,
                                                     long questionId, UUID currentUserId) {
        assertNull(captorValue.getId());
        assertEquals(attributeId, captorValue.getAttributeId());
        assertEquals(maturityLevelId, captorValue.getMaturityLevelId());
        assertEquals(dslImpactWeight, captorValue.getWeight());
        assertEquals(kitVersionId, captorValue.getKitVersionId());
        assertEquals(questionId, captorValue.getQuestionId());
        assertNotNull(captorValue.getCreationTime());
        assertNotNull(captorValue.getLastModificationTime());
        assertEquals(currentUserId, captorValue.getCreatedBy());
        assertEquals(currentUserId, captorValue.getLastModifiedBy());
    }

    private void assertCreateAnswerOptionPortParam(List<CreateAnswerOptionImpactPort.Param> allValues, QuestionImpact qImpact, long kitVersionId, UUID currentUserId) {
        assertThat(allValues)
            .zipSatisfy(qImpact.getOptionImpacts(), (param, optionImpact) -> {
                assertEquals(qImpact.getId(), param.questionImpactId());
                assertEquals(optionImpact.getOptionId(), param.optionId());
                assertEquals(optionImpact.getValue(), param.value());
                assertEquals(kitVersionId, param.kitVersionId());
                assertEquals(currentUserId, param.createdBy());
            });
    }
}
