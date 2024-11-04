package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother;
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
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.*;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.completeKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.createAttribute;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelTwo;
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
    private CreateAnswerOptionImpactPort createAnswerOptionImpactPort;

    @Mock
    private UpdateAnswerOptionImpactPort updateAnswerOptionImpactPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerOptionPort;

    @Mock
    private LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Test
    void testQuestionUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null,
            "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionAdded_AddQuestionAndOptionsToDatabase() {
        var savedQuestionnaire1 = questionnaireWithTitle(QUESTIONNAIRE_TITLE2);
        savedQuestionnaire1.setQuestions(List.of());
        AssessmentKit savedKit = completeKit(List.of(), List.of(), List.of(savedQuestionnaire1));

        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));

        var expectedQuestionId = 251L;
        var expectedQuestionImpactId = 56115L;
        when(createQuestionPort.persist(any())).thenReturn(expectedQuestionId);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(kitContext.answerOption1(), kitContext.answerOption2()));
        when(createQuestionImpactPort.persist(any())).thenReturn(expectedQuestionImpactId);

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(kitContext.questionnaire());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_NEW_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire1, kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var createPortParam = ArgumentCaptor.forClass(CreateQuestionPort.Param.class);
        verify(createQuestionPort, times(1)).persist(createPortParam.capture());
        assertCreateQuestionParam(dslQuestion, createPortParam.getValue(), savedKit, kitContext, currentUserId);

        var createAnswerOptionParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(2)).persist(createAnswerOptionParam.capture());
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().getFirst(), dslAnswerOption1, expectedQuestionId, savedKit, currentUserId);
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().get(1), dslAnswerOption2, expectedQuestionId, savedKit, currentUserId);

        verify(createQuestionImpactPort, times(1)).persist(any());
        verify(createAnswerOptionImpactPort, times(2)).persist(any());

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionAdded_AddImpactsToDatabase() {
        var savedQuestionnaire2 = questionnaireWithTitle(QUESTIONNAIRE_TITLE2);
        savedQuestionnaire2.setQuestions(List.of());
        AssessmentKit savedKit = completeKit(List.of(), List.of(), List.of(savedQuestionnaire2));
        KitContext kitContext = createKitContext();

        var expectedQuestionId = 251L;
        var expectedQuestionImpactId = 56115L;
        when(createQuestionPort.persist(any())).thenReturn(expectedQuestionId);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(kitContext.answerOption1(), kitContext.answerOption2()));
        when(createQuestionImpactPort.persist(any())).thenReturn(expectedQuestionImpactId);

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(kitContext.questionnaire());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_NEW_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire2, kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        verify(createQuestionPort, times(1)).persist(any());
        verify(createAnswerOptionPort, times(2)).persist(any());

        var questionImpactParam = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort, times(1)).persist(questionImpactParam.capture());
        assertCreateQuestionImpactParam(questionImpactParam.getValue(), kitContext.attribute(), kitContext.level(), dslImpact, savedKit, expectedQuestionId, currentUserId);

        var optionImpactParam = ArgumentCaptor.forClass(CreateAnswerOptionImpactPort.Param.class);
        verify(createAnswerOptionImpactPort, times(2)).persist(optionImpactParam.capture());
        assertCreateAnswerOptionImpactParam(expectedQuestionImpactId, optionImpactParam, kitContext.answerOption1, 0D, savedKit, currentUserId);
        assertCreateAnswerOptionImpactParam(expectedQuestionImpactId, optionImpactParam, kitContext.answerOption2, 1D, savedKit, currentUserId);

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        doNothing().when(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_NEW_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);
        var updatePortParam = ArgumentCaptor.forClass(UpdateQuestionPort.Param.class);
        verify(updateQuestionPort, times(1)).update(updatePortParam.capture());

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
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactAdded_AddToDatabase() {
        var levelThree = levelThree();
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level(), levelThree), List.of(kitContext.questionnaire()));

        var expectedQuestionImpactId = 413591L;
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(expectedQuestionImpactId);
        when(createAnswerOptionImpactPort.persist(any(CreateAnswerOptionImpactPort.Param.class))).thenReturn(1L);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(kitContext.answerOption1(), kitContext.answerOption2()));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslMaturityLevelThree = MaturityLevelDslModelMother.domainToDslModel(levelThree());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact1 = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslImpact2 = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelThree, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact1, dslImpact2), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
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

        var optionImpactParam = ArgumentCaptor.forClass(CreateAnswerOptionImpactPort.Param.class);
        verify(createAnswerOptionImpactPort, times(2)).persist(optionImpactParam.capture());
        assertCreateAnswerOptionImpactParam(expectedQuestionImpactId, optionImpactParam, kitContext.answerOption1, 0D, savedKit, currentUserId);
        assertCreateAnswerOptionImpactParam(expectedQuestionImpactId, optionImpactParam, kitContext.answerOption2, 1D, savedKit, currentUserId);

        verifyNoInteractions(
            updateQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionPort,
            updateAnswerOptionImpactPort,
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
        var savedOptionImpact3 = createAnswerOptionImpact(kitContext.answerOption1().getId(), 0.5);
        var savedOptionImpact4 = createAnswerOptionImpact(kitContext.answerOption2().getId(), 0.75);
        savedImpact2.setOptionImpacts(List.of(savedOptionImpact3, savedOptionImpact4));
        kitContext.question().setImpacts(List.of(kitContext.impact(), savedImpact2));
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level(), levelThree), List.of(kitContext.questionnaire()));

        doNothing().when(deleteQuestionImpactPort).delete(savedImpact2.getId(), savedImpact2.getKitVersionId());

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

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
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 2);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

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
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionImpactUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        doNothing().when(updateAnswerOptionImpactPort).update(any(UpdateAnswerOptionImpactPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 0.75D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        ArgumentCaptor<UpdateAnswerOptionImpactPort.Param> updateParam = ArgumentCaptor.forClass(UpdateAnswerOptionImpactPort.Param.class);
        verify(updateAnswerOptionImpactPort, times(1)).update(updateParam.capture());
        assertEquals(kitContext.optionImpact2().getId(), updateParam.getValue().id());
        assertEquals(kitContext.impact().getKitVersionId(), updateParam.getValue().kitVersionId());
        assertEquals(0.75D, updateParam.getValue().value());
        assertNotNull(updateParam.getValue().lastModificationTime());
        assertEquals(currentUserId, updateParam.getValue().lastModifiedBy());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerRangePort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionUpdated_UpdateInDatabase() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of(kitContext.question()));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(kitContext.attribute()))), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        doNothing().when(updateAnswerOptionPort).update(any(UpdateAnswerOptionPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, NEW_OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var updateParam = ArgumentCaptor.forClass(UpdateAnswerOptionPort.Param.class);
        verify(updateAnswerOptionPort, times(1)).update(updateParam.capture());
        assertEquals(kitContext.answerOption1().getId(), updateParam.getValue().id());
        assertEquals(savedKit.getActiveVersionId(), updateParam.getValue().kitVersionId());
        assertEquals(dslAnswerOption1.getCaption(), updateParam.getValue().title());
        assertNotNull(updateParam.getValue().lastModificationTime());
        assertEquals(currentUserId, updateParam.getValue().lastModifiedBy());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            createQuestionPort,
            createAnswerRangePort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_dslHasOneNewQuestion_SaveQuestionWithItsOptions() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of());
        var subject = SubjectMother.subjectWithAttributes("subject1", List.of(kitContext.attribute()));
        var savedKit = AssessmentKitMother.completeKit(List.of(subject), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslSubject = SubjectDslModelMother.domainToDslModel(subject, b -> b.questionnaireCodes(List.of(kitContext.questionnaire().getCode())));

        var dslQuestion = QuestionDslModelMother.domainToDslModel(kitContext.question(), q -> q
            .questionImpacts(List.of(dslImpact))
            .answerOptions(dslAnswerOptionList)
            .questionnaireCode(kitContext.questionnaire().getCode()));

        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(kitContext.questionnaire());
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestion))
            .subjects(List.of(dslSubject))
            .build();

        var expectedQuestionId = 251L;
        var expectedQuestionImpactId = 56115L;
        when(createQuestionPort.persist(any())).thenReturn(expectedQuestionId);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(kitContext.answerOption1(), kitContext.answerOption2()));
        when(createQuestionImpactPort.persist(any())).thenReturn(expectedQuestionImpactId);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        ctx.put(KEY_SUBJECTS, Stream.of(subject).collect(toMap(Subject::getCode, Subject::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var createPortParam = ArgumentCaptor.forClass(CreateQuestionPort.Param.class);
        verify(createQuestionPort, times(1)).persist(createPortParam.capture());
        assertCreateQuestionParam(dslQuestion, createPortParam.getValue(), savedKit, kitContext, currentUserId);

        var createAnswerOptionParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(2)).persist(createAnswerOptionParam.capture());
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().getFirst(), dslAnswerOption1, expectedQuestionId, savedKit, currentUserId);
        assertCreateAnswerOptionParam(createAnswerOptionParam.getAllValues().get(1), dslAnswerOption2, expectedQuestionId, savedKit, currentUserId);

        verify(createQuestionImpactPort, times(1)).persist(any());
        verify(createAnswerOptionImpactPort, times(2)).persist(any());

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_dslHasOneNewQuestion_SaveQuestionImpactsAndOptionImpacts() {
        KitContext kitContext = createKitContext();
        kitContext.questionnaire().setQuestions(List.of());
        var subject = SubjectMother.subjectWithAttributes("subject1", List.of(kitContext.attribute()));
        var savedKit = AssessmentKitMother.completeKit(List.of(subject), List.of(kitContext.level()), List.of(kitContext.questionnaire()));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslSubject = SubjectDslModelMother.domainToDslModel(subject, b -> b.questionnaireCodes(List.of(kitContext.questionnaire().getCode())));

        var dslQuestion = QuestionDslModelMother.domainToDslModel(kitContext.question(), q -> q
            .questionImpacts(List.of(dslImpact))
            .answerOptions(dslAnswerOptionList)
            .questionnaireCode(kitContext.questionnaire().getCode()));

        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(kitContext.questionnaire());
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestion))
            .subjects(List.of(dslSubject))
            .build();

        var expectedQuestionId = 251L;
        var expectedQuestionImpactId = 56115L;
        when(createQuestionPort.persist(any())).thenReturn(expectedQuestionId);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(kitContext.answerOption1(), kitContext.answerOption2()));
        when(createQuestionImpactPort.persist(any())).thenReturn(expectedQuestionImpactId);

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(kitContext.level()).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(kitContext.questionnaire()).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(kitContext.attribute()).collect(toMap(Attribute::getCode, Attribute::getId)));
        ctx.put(KEY_SUBJECTS, Stream.of(subject).collect(toMap(Subject::getCode, Subject::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        verify(createQuestionPort, times(1)).persist(any());
        verify(createAnswerOptionPort, times(2)).persist(any());

        var questionImpactParam = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort, times(1)).persist(questionImpactParam.capture());
        assertCreateQuestionImpactParam(questionImpactParam.getValue(), kitContext.attribute(), kitContext.level(), dslImpact, savedKit, expectedQuestionId, currentUserId);

        var optionImpactParam = ArgumentCaptor.forClass(CreateAnswerOptionImpactPort.Param.class);
        verify(createAnswerOptionImpactPort, times(2)).persist(optionImpactParam.capture());
        assertCreateAnswerOptionImpactParam(expectedQuestionImpactId, optionImpactParam, kitContext.answerOption1, 0D, savedKit, currentUserId);
        assertCreateAnswerOptionImpactParam(expectedQuestionImpactId, optionImpactParam, kitContext.answerOption2, 1D, savedKit, currentUserId);

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );
    }

    private KitContext createKitContext() {
        var levelTwo = levelTwo();
        var questionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var question = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, questionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var impact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, question.getId());
        var answerOption1 = createAnswerOption(question.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(question.getId(), OPTION_TITLE, OPTION_INDEX2);
        var optionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var optionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        impact.setOptionImpacts(List.of(optionImpact1, optionImpact2));
        question.setOptions(List.of(answerOption1, answerOption2));
        question.setImpacts(List.of(impact));
        questionnaire.setQuestions(List.of(question));
        return new KitContext(levelTwo, questionnaire, question, attribute, impact, answerOption1, answerOption2, optionImpact2);
    }

    private void assertCreateQuestionParam(QuestionDslModel dslQuestion, CreateQuestionPort.Param createPortParam, AssessmentKit savedKit, KitContext kitContext, UUID currentUserId) {
        assertEquals(dslQuestion.getCode(), createPortParam.code());
        assertEquals(dslQuestion.getTitle(), createPortParam.title());
        assertEquals(dslQuestion.getIndex(), createPortParam.index());
        assertEquals(dslQuestion.getDescription(), createPortParam.hint());
        assertEquals(dslQuestion.isMayNotBeApplicable(), createPortParam.mayNotBeApplicable());
        assertEquals(dslQuestion.isAdvisable(), createPortParam.advisable());
        assertEquals(savedKit.getActiveVersionId(), createPortParam.kitVersionId());
        assertEquals(kitContext.questionnaire().getId(), createPortParam.questionnaireId());
        assertEquals(currentUserId, createPortParam.createdBy());
    }

    private void assertCreateQuestionImpactParam(QuestionImpact questionImpactParam, Attribute attribute, MaturityLevel level, QuestionImpactDslModel dslImpact, AssessmentKit savedKit, long expectedQuestionId, UUID currentUserId) {
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

    private void assertCreateAnswerOptionParam(CreateAnswerOptionPort.Param createAnswerOptionParam, AnswerOptionDslModel dslAnswerOption1, long expectedQuestionId, AssessmentKit savedKit, UUID currentUserId) {
        assertEquals(dslAnswerOption1.getCaption(), createAnswerOptionParam.title());
        assertEquals(dslAnswerOption1.getIndex(), createAnswerOptionParam.index());
        assertEquals(expectedQuestionId, createAnswerOptionParam.questionId());
        assertEquals(savedKit.getActiveVersionId(), createAnswerOptionParam.kitVersionId());
        assertEquals(currentUserId, createAnswerOptionParam.createdBy());
    }

    private void assertCreateAnswerOptionImpactParam(long expectedQuestionImpactId, ArgumentCaptor<CreateAnswerOptionImpactPort.Param> optionImpactParams, AnswerOption answerOption, double value, AssessmentKit savedKit, UUID currentUserId) {
        var optionImpactParam = optionImpactParams.getAllValues().stream()
            .filter(x -> x.value() == value)
            .findFirst()
            .orElseThrow(AssertionError::new);
        assertEquals(expectedQuestionImpactId, optionImpactParam.questionImpactId());
        assertEquals(answerOption.getId(), optionImpactParam.optionId());
        assertEquals(value, optionImpactParam.value());
        assertEquals(savedKit.getActiveVersionId(), optionImpactParam.kitVersionId());
        assertEquals(currentUserId, optionImpactParam.createdBy());
    }

    private record KitContext(
        MaturityLevel level,
        Questionnaire questionnaire,
        Question question,
        Attribute attribute,
        QuestionImpact impact,
        AnswerOption answerOption1,
        AnswerOption answerOption2,
        AnswerOptionImpact optionImpact2) {
    }
}
