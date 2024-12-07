package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.flickit.assessment.kit.test.fixture.application.dsl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.*;
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
        Long kitVersionId = 1L;
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
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, null, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .questions(List.of(dslQuestion))
            .build();

        var createQuestionParam = new CreateQuestionPort.Param(
            dslQuestion.getCode(),
            dslQuestion.getTitle(),
            dslQuestion.getIndex(),
            dslQuestion.getDescription(),
            dslQuestion.isMayNotBeApplicable(),
            dslQuestion.isAdvisable(),
            kitVersionId,
            questionnaire.getId(),
            answerRangeId,
            currentUserId
        );

        var createAnswerRangeParam = new CreateAnswerRangePort.Param(kitVersionId, null, null, false, currentUserId);

        when(createAnswerRangePort.persist(createAnswerRangeParam)).thenReturn(answerRangeId);
        when(createQuestionPort.persist(createQuestionParam)).thenReturn(question.getId());
        var createOption1Param = new CreateAnswerOptionPort.Param(answerOption1.getTitle(), answerOption1.getIndex(), answerRangeId, OPTION_VALUE1, kitVersionId, currentUserId);
        var createOption2Param = new CreateAnswerOptionPort.Param(answerOption2.getTitle(), answerOption2.getIndex(), answerRangeId, OPTION_VALUE2, kitVersionId, currentUserId);
        when(createAnswerOptionPort.persist(createOption1Param)).thenReturn(answerOption1.getId());
        when(createAnswerOptionPort.persist(createOption2Param)).thenReturn(answerOption2.getId());

        var createImpact = new QuestionImpact(
            null,
            attribute.getId(),
            levelTwo.getId(),
            dslImpact.getWeight(),
            kitVersionId,
            question.getId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId,
            currentUserId
        );
        when(createQuestionImpactPort.persist(createImpact)).thenReturn(impact.getId());

        var createOptionImpact1Param = new CreateAnswerOptionImpactPort.Param(impact.getId(), optionImpact1.getOptionId(), optionImpact1.getValue(), kitVersionId, currentUserId);
        var createOptionImpact2Param = new CreateAnswerOptionImpactPort.Param(impact.getId(), optionImpact2.getOptionId(), optionImpact2.getValue(), kitVersionId, currentUserId);
        when(createAnswerOptionImpactPort.persist(createOptionImpact1Param)).thenReturn(optionImpact1.getId());
        when(createAnswerOptionImpactPort.persist(createOptionImpact2Param)).thenReturn(optionImpact2.getId());

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_QUESTIONNAIRES, Map.of(questionnaire.getCode(), questionnaire.getId()));
        context.put(KEY_ATTRIBUTES, Map.of(attribute.getCode(), attribute.getId()));
        context.put(KEY_MATURITY_LEVELS, Map.of(levelTwo.getCode(), levelTwo.getId()));

        persister.persist(context, dslModel, kitVersionId, currentUserId);

        // TODO: assert?
    }

    @Test
    void testQuestionCreateKitPersister_WhenQuestionDslHasAnswerRange_ThenSaveQuestion() {
        long kitVersionId = 1;
        UUID currentUserId = UUID.randomUUID();

        Questionnaire questionnaire = QuestionnaireMother.questionnaireWithTitle("devops");
        AnswerRange answerRange = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions(1);
        Question question = QuestionMother.createQuestion(answerRange.getId(), questionnaire.getId());
        Attribute attr = AttributeMother.attributeWithTitle("flexibility");
        MaturityLevel mLevel = MaturityLevelMother.levelOne();
        QuestionImpact qImpact = createQuestionImpact(attr.getId(), mLevel.getId(), 1, question.getId());
        AnswerOption answerOption1 = answerRange.getAnswerOptions().getFirst();
        AnswerOption answerOption2 = answerRange.getAnswerOptions().getLast();
        AnswerOptionImpact aoi1 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption1.getId(), answerOption1.getValue());
        AnswerOptionImpact aoi2 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption2.getId(), answerOption2.getValue());
        qImpact.setOptionImpacts(List.of(aoi1, aoi2));
        question.setImpacts(List.of(qImpact));
        question.setOptions(null);

        MaturityLevelDslModel maturityLevelDslModel = MaturityLevelDslModelMother.domainToDslModel(mLevel);
        Map<Integer, Double> optionIndexToValue = new HashMap<>();
        optionIndexToValue.put(answerOption1.getIndex(), answerOption1.getValue());
        optionIndexToValue.put(answerOption2.getIndex(), answerOption2.getValue());
        QuestionImpactDslModel questionImpactDslModel = QuestionImpactDslModelMother.questionImpactDslModel(attr.getCode(),
            maturityLevelDslModel,
            null,
            optionIndexToValue,
            qImpact.getWeight());

        QuestionDslModel questionDslModel = QuestionDslModelMother.domainToDslModel(question,
            b -> b.questionImpacts(List.of(questionImpactDslModel))
                .answerRangeCode(answerRange.getCode())
                .questionnaireCode(questionnaire.getCode()));

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_QUESTIONNAIRES, Map.of(questionnaire.getCode(), questionnaire.getId()));
        context.put(KEY_ATTRIBUTES, Map.of(attr.getCode(), attr.getId()));
        context.put(KEY_MATURITY_LEVELS, Map.of(mLevel.getCode(), mLevel.getId()));
        context.put(KEY_ANSWER_RANGES, Map.of(answerRange.getCode(), answerRange.getId()));

        AssessmentKitDslModel kitDslModel = AssessmentKitDslModel.builder()
            .questions(List.of(questionDslModel))
            .build();

        when(createQuestionPort.persist(any(CreateQuestionPort.Param.class))).thenReturn(question.getId());
        when(loadAnswerOptionsPort.loadByRangeId(answerRange.getId(), kitVersionId))
            .thenReturn(List.of(answerOption1, answerOption2));
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(qImpact.getId());
        when(createAnswerOptionImpactPort.persist(any(CreateAnswerOptionImpactPort.Param.class))).thenReturn(anyLong());

        persister.persist(context, kitDslModel, kitVersionId, currentUserId);

        verifyNoInteractions(createAnswerRangePort, createAnswerOptionPort);

        verify(createQuestionPort).persist(createQuestionParamCaptor.capture());
        CreateQuestionPort.Param createQuestionPortParam = createQuestionParamCaptor.getValue();
        assertEquals(questionDslModel.getCode(), createQuestionPortParam.code());
        assertEquals(questionDslModel.getTitle(), createQuestionPortParam.title());
        assertEquals(questionDslModel.getIndex(), createQuestionPortParam.index());
        assertEquals(questionDslModel.getDescription(), createQuestionPortParam.hint());
        assertEquals(questionDslModel.isMayNotBeApplicable(), createQuestionPortParam.mayNotBeApplicable());
        assertEquals(questionDslModel.isAdvisable(), createQuestionPortParam.advisable());
        assertEquals(kitVersionId, createQuestionPortParam.kitVersionId());
        assertEquals(questionnaire.getId(), createQuestionPortParam.questionnaireId());
        assertEquals(answerRange.getId(), createQuestionPortParam.answerRangeId());
        assertEquals(currentUserId, createQuestionPortParam.createdBy());

        verify(createQuestionImpactPort).persist(questionImpactCaptor.capture());
        QuestionImpact questionImpactCaptorValue = questionImpactCaptor.getValue();
        assertNull(questionImpactCaptorValue.getId());
        assertEquals(attr.getId(), questionImpactCaptorValue.getAttributeId());
        assertEquals(mLevel.getId(), questionImpactCaptorValue.getMaturityLevelId());
        assertEquals(questionImpactDslModel.getWeight(), questionImpactCaptorValue.getWeight());
        assertEquals(kitVersionId, questionImpactCaptorValue.getKitVersionId());
        assertEquals(question.getId(), questionImpactCaptorValue.getQuestionId());
        assertNotNull(questionImpactCaptorValue.getCreationTime());
        assertNotNull(questionImpactCaptorValue.getLastModificationTime());
        assertEquals(currentUserId, questionImpactCaptorValue.getCreatedBy());
        assertEquals(currentUserId, questionImpactCaptorValue.getLastModifiedBy());

        verify(createAnswerOptionImpactPort, times(2)).persist(createAnswerOptionImpactParamCaptor.capture());
        List<CreateAnswerOptionImpactPort.Param> allValues = createAnswerOptionImpactParamCaptor.getAllValues();
        for (int i = 0; i < allValues.size(); i++) {
            assertEquals(qImpact.getId(), allValues.get(i).questionImpactId());
            assertEquals(qImpact.getOptionImpacts().get(i).getOptionId(), allValues.get(i).optionId());
            assertEquals(qImpact.getOptionImpacts().get(i).getValue(), allValues.get(i).value());
            assertEquals(kitVersionId, allValues.get(i).kitVersionId());
            assertEquals(currentUserId, allValues.get(i).createdBy());
        }
    }
}
