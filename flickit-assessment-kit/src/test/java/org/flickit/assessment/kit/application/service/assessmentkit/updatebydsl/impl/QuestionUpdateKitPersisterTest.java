package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.*;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.optionOne;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.optionTwo;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.completeKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.createAttribute;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.kit.test.fixture.application.MeasureMother.measureFromQuestionnaire;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithAttributes;
import static org.flickit.assessment.kit.test.fixture.application.dsl.AnswerOptionDslModelMother.answerOptionDslModel;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother.questionDslModel;
import static org.flickit.assessment.kit.test.fixture.application.dsl.QuestionImpactDslModelMother.questionImpactDslModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionUpdateKitPersisterTest {

    @InjectMocks
    private QuestionUpdateKitPersister persister;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private CreateQuestionPort createQuestionPort;

    @Mock
    private CreateQuestionImpactPort createQuestionImpactPort;

    @Mock
    private DeleteQuestionImpactPort deleteQuestionImpactPort;

    @Mock
    private UpdateQuestionImpactPort updateQuestionImpactPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerOptionPort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(7, persister.order());
    }

    @Test
    void testQuestionUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        KitContext kitContext = createKitContext();
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))),
            List.of(kitContext.level()),
            List.of(kitContext.questionnaire()),
            List.of(kitContext.measure()),
            List.of());

        AssessmentKitDslModel dslKit = createKitDslModel(QUESTION_TITLE1, 1, 1, optionOne().getTitle());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_MEASURE, Stream.of(kitContext.measure()).collect(toMap(Measure::getCode, Measure::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionAddedWithNewQuestionnaire_AddQuestionAndOptionsToDatabase() {
        var savedQuestionnaire1 = questionnaireWithTitle("DevOps");
        var measure = measureFromQuestionnaire(savedQuestionnaire1);
        savedQuestionnaire1.setQuestions(List.of());
        AssessmentKit savedKit = completeKit(List.of(), List.of(), List.of(savedQuestionnaire1), List.of(measure), List.of());

        KitContext kitContext = createKitContext();

        var expectedAnswerRangeId = kitContext.answerOption1.getAnswerRangeId();
        var expectedQuestionId = 251L;
        var expectedQuestionImpactId = 56115L;
        when(createAnswerRangePort.persist(any())).thenReturn(expectedAnswerRangeId);
        when(createQuestionPort.persist(any())).thenReturn(expectedQuestionId);
        when(createQuestionImpactPort.persist(any())).thenReturn(expectedQuestionImpactId);

        AssessmentKitDslModel dslKit = createKitDslModel(QUESTION_TITLE1, 1, 1, optionOne().getTitle());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire1, kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_MEASURE, Stream.of(kitContext.measure()).collect(toMap(Measure::getCode, Measure::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var createAnswerRangePortParam = ArgumentCaptor.forClass(CreateAnswerRangePort.Param.class);
        verify(createAnswerRangePort, times(1)).persist(createAnswerRangePortParam.capture());
        assertCreateAnswerRangeParam(savedKit, createAnswerRangePortParam, currentUserId);

        var createPortParam = ArgumentCaptor.forClass(CreateQuestionPort.Param.class);
        verify(createQuestionPort, times(1)).persist(createPortParam.capture());
        QuestionDslModel dslQuestion = dslKit.getQuestions().getFirst();
        assertCreateQuestionParam(dslQuestion, createPortParam.getValue(), savedKit, kitContext, expectedAnswerRangeId, currentUserId);

        var createAnswerOptionParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(2)).persist(createAnswerOptionParam.capture());
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().getFirst(), dslQuestion.getAnswerOptions().getFirst(), savedKit, expectedAnswerRangeId, currentUserId);
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().get(1), dslQuestion.getAnswerOptions().get(1), savedKit, expectedAnswerRangeId, currentUserId);

        var questionImpactParam = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort, times(1)).persist(questionImpactParam.capture());
        QuestionImpactDslModel dslImpact = dslKit.getQuestions().getFirst().getQuestionImpacts().getFirst();
        assertCreateQuestionImpactParam(questionImpactParam.getValue(), kitContext.attribute(), kitContext.level(), dslImpact, savedKit, expectedQuestionId, currentUserId);

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        var savedQuestion = kitContext.question;
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))),
            List.of(kitContext.level()),
            List.of(kitContext.questionnaire()),
            List.of(kitContext.measure()),
            List.of());

        doNothing().when(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));

        AssessmentKitDslModel dslKit = createKitDslModel(QUESTION_NEW_TITLE1,
            1,
            1,
            savedQuestion.getOptions().getFirst().getTitle());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);
        var updatePortParam = ArgumentCaptor.forClass(UpdateQuestionPort.Param.class);
        verify(updateQuestionPort, times(1)).update(updatePortParam.capture());

        QuestionDslModel dslQuestion = dslKit.getQuestions().getFirst();
        assertEquals(kitContext.question().getId(), updatePortParam.getValue().id());
        assertEquals(savedKit.getActiveVersionId(), updatePortParam.getValue().kitVersionId());
        assertEquals(dslQuestion.getCode(), updatePortParam.getValue().code());
        assertEquals(dslQuestion.getTitle(), updatePortParam.getValue().title());
        assertEquals(dslQuestion.getIndex(), updatePortParam.getValue().index());
        assertEquals(dslQuestion.getDescription(), updatePortParam.getValue().hint());
        assertEquals(dslQuestion.isMayNotBeApplicable(), updatePortParam.getValue().mayNotBeApplicable());
        assertEquals(dslQuestion.isAdvisable(), updatePortParam.getValue().advisable());
        assertNotNull(updatePortParam.getValue().lastModificationTime());
        assertEquals(currentUserId, updatePortParam.getValue().lastModifiedBy());

        verifyNoInteractions(
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactAdded_AddToDatabase() {
        var levelThree = levelThree();
        KitContext kitContext = createKitContext();
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))),
            List.of(kitContext.level(), levelThree),
            List.of(kitContext.questionnaire()),
            List.of(kitContext.measure()),
            List.of());

        var expectedQuestionImpactId = 413591L;
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(expectedQuestionImpactId);

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslMaturityLevelThree = MaturityLevelDslModelMother.domainToDslModel(levelThree());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, optionOne().getTitle(), optionOne().getValue());
        var dslAnswerOption2 = answerOptionDslModel(2, optionTwo().getTitle(), optionTwo().getValue());
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact1 = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslImpact2 = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelThree, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1,
            List.of(dslImpact1, dslImpact2), dslAnswerOptionList, null, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level(), levelThree).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var questionImpactParam = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort, times(1)).persist(questionImpactParam.capture());
        assertCreateQuestionImpactParam(questionImpactParam.getValue(), kitContext.attribute(), levelThree, dslImpact2, savedKit, kitContext.question().getId(), currentUserId);

        verifyNoInteractions(
            updateQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactDeleted_DeleteFromDatabase() {
        KitContext kitContext = createKitContext();
        var levelThree = levelThree();
        var savedImpact2 = createQuestionImpact(kitContext.attribute().getId(), levelThree.getId(), 1, kitContext.question().getId());
        kitContext.question().setImpacts(List.of(kitContext.impact(), savedImpact2));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))),
            List.of(kitContext.level(), levelThree),
            List.of(kitContext.questionnaire()),
            List.of(kitContext.measure()),
            List.of());

        doNothing().when(deleteQuestionImpactPort).delete(savedImpact2.getId(), savedImpact2.getKitVersionId());

        AssessmentKitDslModel dslKit = createKitDslModel(QUESTION_TITLE1, 1, 1, optionOne().getTitle());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level(), levelThree).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verify(deleteQuestionImpactPort, times(1)).delete(savedImpact2.getId(), savedImpact2.getKitVersionId());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))),
            List.of(kitContext.level()),
            List.of(kitContext.questionnaire()),
            List.of(kitContext.measure()),
            List.of());

        AssessmentKitDslModel dslKit = createKitDslModel(QUESTION_TITLE1, 2, 1, optionOne().getTitle());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        QuestionImpactDslModel dslImpact = dslKit.getQuestions().getFirst().getQuestionImpacts().getFirst();
        var updateWeightParam = ArgumentCaptor.forClass(UpdateQuestionImpactPort.UpdateWeightParam.class);
        verify(updateQuestionImpactPort, times(1)).updateWeight(updateWeightParam.capture());
        assertEquals(kitContext.impact().getId(), updateWeightParam.getValue().id());
        assertEquals(kitContext.impact().getKitVersionId(), updateWeightParam.getValue().kitVersionId());
        assertEquals(dslImpact.getWeight(), updateWeightParam.getValue().weight());
        assertEquals(kitContext.impact().getQuestionId(), updateWeightParam.getValue().questionId());
        assertNotNull(updateWeightParam.getValue().lastModificationTime());
        assertEquals(currentUserId, updateWeightParam.getValue().lastModifiedBy());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))),
            List.of(kitContext.level()),
            List.of(kitContext.questionnaire()),
            List.of(kitContext.measure()),
            List.of());

        doNothing().when(updateAnswerOptionPort).updateTitle(any(UpdateAnswerOptionPort.UpdateTitleParam.class));

        AssessmentKitDslModel dslKit = createKitDslModel(QUESTION_TITLE1, 1, 0.7, "newOne");

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var updateParam = ArgumentCaptor.forClass(UpdateAnswerOptionPort.UpdateTitleParam.class);
        verify(updateAnswerOptionPort, times(1)).updateTitle(updateParam.capture());
        assertEquals(kitContext.answerOption1().getId(), updateParam.getValue().answerOptionId());
        assertEquals(savedKit.getActiveVersionId(), updateParam.getValue().kitVersionId());
        assertEquals("newOne", updateParam.getValue().title());
        assertNotNull(updateParam.getValue().lastModificationTime());
        assertEquals(currentUserId, updateParam.getValue().lastModifiedBy());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createQuestionPort,
            createAnswerRangePort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionAddedWithOldQuestionnaire_SaveQuestionWithItsOptions() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of());
        var savedKit = AssessmentKitMother.completeKit(List.of(), List.of(kitContext.level()),
            List.of(kitContext.questionnaire()), List.of(kitContext.measure()), List.of());

        var dslKit = createKitDslModel(QUESTION_TITLE1, 1, 1, optionOne().getTitle());

        var expectedAnswerRangeId = kitContext.answerOption1.getAnswerRangeId();
        var expectedQuestionId = 251L;
        var expectedQuestionImpactId = 56115L;
        when(createAnswerRangePort.persist(any())).thenReturn(expectedAnswerRangeId);
        when(createQuestionPort.persist(any())).thenReturn(expectedQuestionId);
        when(createQuestionImpactPort.persist(any())).thenReturn(expectedQuestionImpactId);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_MEASURE, Stream.of(kitContext.measure()).collect(toMap(Measure::getCode, Measure::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var createAnswerRangePortParam = ArgumentCaptor.forClass(CreateAnswerRangePort.Param.class);
        verify(createAnswerRangePort, times(1)).persist(createAnswerRangePortParam.capture());
        assertCreateAnswerRangeParam(savedKit, createAnswerRangePortParam, currentUserId);

        var createPortParam = ArgumentCaptor.forClass(CreateQuestionPort.Param.class);
        verify(createQuestionPort, times(1)).persist(createPortParam.capture());
        QuestionDslModel dslQuestion = dslKit.getQuestions().getFirst();
        assertCreateQuestionParam(dslQuestion, createPortParam.getValue(), savedKit, kitContext, expectedAnswerRangeId, currentUserId);

        var createAnswerOptionParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(2)).persist(createAnswerOptionParam.capture());
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().getFirst(), dslQuestion.getAnswerOptions().getFirst(), savedKit, expectedAnswerRangeId, currentUserId);
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().get(1), dslQuestion.getAnswerOptions().get(1), savedKit, expectedAnswerRangeId, currentUserId);

        var questionImpactParam = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort, times(1)).persist(questionImpactParam.capture());
        QuestionImpactDslModel dslImpact = dslKit.getQuestions().getFirst().getQuestionImpacts().getFirst();
        assertCreateQuestionImpactParam(questionImpactParam.getValue(), kitContext.attribute(), kitContext.level(), dslImpact, savedKit, expectedQuestionId, currentUserId);

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionPort
        );
    }

    private KitContext createKitContext() {
        var levelTwo = levelTwo();
        var questionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var measure = measureFromQuestionnaire(questionnaire);
        var question = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, 25L, questionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var impact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, question.getId());
        var answerOption1 = optionOne(question.getAnswerRangeId());
        var answerOption2 = optionTwo(question.getAnswerRangeId());
        question.setOptions(List.of(answerOption1, answerOption2));
        question.setImpacts(List.of(impact));
        questionnaire.setQuestions(List.of(question));
        return new KitContext(levelTwo, questionnaire, measure, question, attribute, impact, answerOption1, answerOption2);
    }

    private AssessmentKitDslModel createKitDslModel(String questionTitle,
                                                    int questionWeight,
                                                    double secondOptionValue,
                                                    String firstOptionTitle) {
        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, firstOptionTitle, optionOne().getValue());
        var dslAnswerOption2 = answerOptionDslModel(2, optionTwo().getTitle(), optionTwo().getValue());
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), secondOptionValue);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, questionWeight);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, questionTitle, null,
            "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, null, Boolean.FALSE, Boolean.TRUE);
        return AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();
    }

    private void assertCreateAnswerRangeParam(AssessmentKit savedKit,
                                              ArgumentCaptor<CreateAnswerRangePort.Param> createAnswerRangePortParam,
                                              UUID currentUserId) {
        assertEquals(savedKit.getActiveVersionId(), createAnswerRangePortParam.getValue().kitVersionId());
        assertNull(createAnswerRangePortParam.getValue().title());
        assertFalse(createAnswerRangePortParam.getValue().reusable());
        assertEquals(currentUserId, createAnswerRangePortParam.getValue().createdBy());
    }

    private void assertCreateQuestionParam(QuestionDslModel dslQuestion,
                                           CreateQuestionPort.Param createPortParam,
                                           AssessmentKit savedKit,
                                           KitContext kitContext,
                                           long answerRangeId,
                                           UUID currentUserId) {
        assertEquals(dslQuestion.getCode(), createPortParam.code());
        assertEquals(dslQuestion.getTitle(), createPortParam.title());
        assertEquals(dslQuestion.getIndex(), createPortParam.index());
        assertEquals(dslQuestion.getDescription(), createPortParam.hint());
        assertEquals(dslQuestion.isMayNotBeApplicable(), createPortParam.mayNotBeApplicable());
        assertEquals(dslQuestion.isAdvisable(), createPortParam.advisable());
        assertEquals(savedKit.getActiveVersionId(), createPortParam.kitVersionId());
        assertEquals(kitContext.questionnaire().getId(), createPortParam.questionnaireId());
        assertEquals(answerRangeId, createPortParam.answerRangeId());
        assertEquals(currentUserId, createPortParam.createdBy());
    }

    private void assertCreateQuestionImpactParam(QuestionImpact questionImpactParam,
                                                 Attribute attribute,
                                                 MaturityLevel level,
                                                 QuestionImpactDslModel dslImpact,
                                                 AssessmentKit savedKit,
                                                 long expectedQuestionId,
                                                 UUID currentUserId) {
        assertNull(questionImpactParam.getId());
        assertEquals(attribute.getId(), questionImpactParam.getAttributeId());
        assertEquals(level.getId(), questionImpactParam.getMaturityLevelId());
        assertEquals(dslImpact.getWeight(), questionImpactParam.getWeight());
        assertEquals(savedKit.getActiveVersionId(), questionImpactParam.getKitVersionId());
        assertEquals(expectedQuestionId, questionImpactParam.getQuestionId());
        assertNotNull(questionImpactParam.getCreationTime());
        assertNotNull(questionImpactParam.getLastModificationTime());
        assertEquals(currentUserId, questionImpactParam.getCreatedBy());
        assertEquals(currentUserId, questionImpactParam.getLastModifiedBy());
    }

    private void assertCreateAnswerOptionParam(CreateAnswerOptionPort.Param createAnswerOptionParam,
                                               AnswerOptionDslModel dslAnswerOption1,
                                               AssessmentKit savedKit,
                                               long answerRangeId,
                                               UUID currentUserId) {
        assertEquals(dslAnswerOption1.getCaption(), createAnswerOptionParam.title());
        assertEquals(answerRangeId, createAnswerOptionParam.answerRangeId());
        assertEquals(dslAnswerOption1.getIndex(), createAnswerOptionParam.index());
        assertEquals(savedKit.getActiveVersionId(), createAnswerOptionParam.kitVersionId());
        assertEquals(currentUserId, createAnswerOptionParam.createdBy());
    }

    private record KitContext(
        MaturityLevel level,
        Questionnaire questionnaire,
        Measure measure,
        Question question,
        Attribute attribute,
        QuestionImpact impact,
        AnswerOption answerOption1,
        AnswerOption answerOption2) {
    }
}
