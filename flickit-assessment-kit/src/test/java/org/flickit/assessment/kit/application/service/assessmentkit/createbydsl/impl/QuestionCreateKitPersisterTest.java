package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.jetbrains.annotations.NotNull;
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

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_ANSWER_RANGES;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_MATURITY_LEVELS;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_ATTRIBUTES;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.optionOne;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.optionTwo;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.attributeWithTitle;
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
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Captor
    private ArgumentCaptor<CreateQuestionPort.Param> createQuestionParamCaptor;

    @Captor
    private ArgumentCaptor<QuestionImpact> questionImpactCaptor;

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
        var questionnaire = QuestionnaireMother.questionnaireWithTitle("DevOps");
        var question = createQuestion(answerRangeId, questionnaire.getId());
        var attribute = attributeWithTitle("Agility");
        var impact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, question.getId());
        var optionOne = optionOne(question.getAnswerRangeId());
        var optionTwo = optionTwo(question.getAnswerRangeId());
        question.setOptions(List.of(optionOne, optionTwo));
        question.setImpacts(List.of(impact));
        questionnaire.setQuestions(List.of(question));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslAnswerOption1 = answerOptionDslModel(1, optionOne.getTitle(), optionOne.getValue());
        var dslAnswerOption2 = answerOptionDslModel(2, optionTwo.getTitle(), optionTwo.getValue());
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(attribute.getCode(), dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(question.getCode(), 1, question.getTitle(), null, questionnaire.getCode(),
            List.of(dslImpact), dslAnswerOptionList, null, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .questions(List.of(dslQuestion))
            .build();

        var createAnswerRangeParam = new CreateAnswerRangePort.Param(kitVersionId, null, null, false, currentUserId);

        when(createAnswerRangePort.persist(createAnswerRangeParam)).thenReturn(answerRangeId);
        when(createQuestionPort.persist(any())).thenReturn(question.getId());
        var createOption1Param = createOptionPortParam(optionOne, kitVersionId, currentUserId);
        var createOption2Param = createOptionPortParam(optionTwo, kitVersionId, currentUserId);

        when(createAnswerOptionPort.persist(createOption1Param)).thenReturn(optionOne.getId());
        when(createAnswerOptionPort.persist(createOption2Param)).thenReturn(optionTwo.getId());

        when(createQuestionImpactPort.persist(any())).thenReturn(impact.getId());

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
    }

    @Test
    void testPersist_WhenQuestionDslHasAnswerRange_ThenSaveQuestion() {
        long kitVersionId = 1;
        UUID currentUserId = UUID.randomUUID();

        var questionnaire = QuestionnaireMother.questionnaireWithTitle("devops");
        var answerRange = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions(1);
        var question = QuestionMother.createQuestion(answerRange.getId(), questionnaire.getId());
        var attribute = attributeWithTitle("flexibility");
        var maturityLevel = MaturityLevelMother.levelOne();
        var impact = createQuestionImpact(attribute.getId(), maturityLevel.getId(), 1, question.getId());
        var answerOption1 = answerRange.getAnswerOptions().getFirst();
        var answerOption2 = answerRange.getAnswerOptions().getLast();
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
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(impact.getId());

        persister.persist(context, kitDslModel, kitVersionId, currentUserId);

        verifyNoInteractions(createAnswerRangePort, createAnswerOptionPort);

        verify(createQuestionPort).persist(createQuestionParamCaptor.capture());
        CreateQuestionPort.Param createQuestionParamCaptorValue = createQuestionParamCaptor.getValue();
        assertCreateQuestionPortParam(createQuestionParamCaptorValue, questionDslModel, kitVersionId, questionnaire.getId(), answerRange.getId(), currentUserId);

        verify(createQuestionImpactPort).persist(questionImpactCaptor.capture());
        QuestionImpact questionImpactCaptorValue = questionImpactCaptor.getValue();
        assertCreateQuestionImpactPortParam(questionImpactCaptorValue, attribute.getId(), maturityLevel.getId(), dslImpact.getWeight(), kitVersionId, question.getId(), currentUserId);
    }

    @NotNull
    private static CreateAnswerOptionPort.Param createOptionPortParam(AnswerOption option, long kitVersionId, UUID currentUserId) {
        return new CreateAnswerOptionPort.Param(option.getTitle(),
            option.getIndex(),
            option.getAnswerRangeId(),
            option.getValue(),
            kitVersionId,
            currentUserId);
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
}
