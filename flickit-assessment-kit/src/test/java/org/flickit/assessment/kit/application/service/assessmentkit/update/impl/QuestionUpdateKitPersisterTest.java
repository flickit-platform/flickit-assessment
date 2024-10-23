package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.*;
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

    @Test
    void testQuestionUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, Boolean.FALSE, Boolean.TRUE, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));


        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
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
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionAdded_AddToDatabase() {
        var savedQuestionnaire1 = questionnaireWithTitle(QUESTIONNAIRE_TITLE2);
        savedQuestionnaire1.setQuestions(List.of());
        AssessmentKit savedKit = completeKit(List.of(), List.of(), List.of(savedQuestionnaire1));

        var levelTwo = levelTwo();
        var savedQuestionnaire2 = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_OLD_TITLE1, 1, null, false, true, savedQuestionnaire2.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire2.setQuestions(List.of(savedQuestion));

        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(answerOption1, answerOption2));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(savedQuestionnaire2);
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire1, savedQuestionnaire2).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_OLD_TITLE1, 1, null, false, true, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));

        doNothing().when(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);
        var updatePortParam = ArgumentCaptor.forClass(UpdateQuestionPort.Param.class);
        verify(updateQuestionPort, times(1)).update(updatePortParam.capture());

        assertEquals(savedQuestion.getId(), updatePortParam.getValue().id());
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
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactAdded_AddToDatabase() {
        var levelTwo = levelTwo();
        var levelThree = levelThree();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo, levelThree), List.of(savedQuestionnaire));

        var expectedQuestionImpactId = 413591L;
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(expectedQuestionImpactId);
        when(createAnswerOptionImpactPort.persist(any(CreateAnswerOptionImpactPort.Param.class))).thenReturn(1L);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(answerOption1, answerOption2));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslMaturityLevelThree = MaturityLevelDslModelMother.domainToDslModel(levelThree());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact1 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslImpact2 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelThree, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact1, dslImpact2), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo, levelThree).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        UUID currentUserId = UUID.randomUUID();
        persister.persist(ctx, savedKit, dslKit, currentUserId);

        var questionImpactParam = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort, times(1)).persist(questionImpactParam.capture());

        assertNull(questionImpactParam.getValue().getId());
        assertEquals(attribute.getId(), questionImpactParam.getValue().getAttributeId());
        assertEquals(levelThree.getId(), questionImpactParam.getValue().getMaturityLevelId());
        assertEquals(dslImpact2.getWeight(), questionImpactParam.getValue().getWeight());
        assertEquals(savedKit.getActiveVersionId(), questionImpactParam.getValue().getKitVersionId());
        assertEquals(savedQuestion.getId(), questionImpactParam.getValue().getQuestionId());
        assertNotNull(questionImpactParam.getValue().getCreationTime());
        assertNotNull(questionImpactParam.getValue().getLastModificationTime());
        assertEquals(currentUserId, questionImpactParam.getValue().getCreatedBy());
        assertEquals(currentUserId, questionImpactParam.getValue().getLastModifiedBy());

        var optionImpactParam = ArgumentCaptor.forClass(CreateAnswerOptionImpactPort.Param.class);
        verify(createAnswerOptionImpactPort, times(2)).persist(optionImpactParam.capture());
        CreateAnswerOptionImpactPort.Param firstOptionImpactParam = optionImpactParam.getAllValues().stream()
            .filter(x -> x.value() == 0D)
            .findFirst().get();
        assertEquals(expectedQuestionImpactId, firstOptionImpactParam.questionImpactId());
        assertEquals(answerOption1.getId(), firstOptionImpactParam.optionId());
        assertEquals(0D, firstOptionImpactParam.value());
        assertEquals(savedKit.getActiveVersionId(), firstOptionImpactParam.kitVersionId());
        assertEquals(currentUserId, firstOptionImpactParam.createdBy());

        CreateAnswerOptionImpactPort.Param secondOptionImpactParam = optionImpactParam.getAllValues().stream()
            .filter(x -> x.value() == 1D)
            .findFirst().get();
        assertEquals(expectedQuestionImpactId, secondOptionImpactParam.questionImpactId());
        assertEquals(answerOption2.getId(), secondOptionImpactParam.optionId());
        assertEquals(1D, secondOptionImpactParam.value());
        assertEquals(savedKit.getActiveVersionId(), secondOptionImpactParam.kitVersionId());
        assertEquals(currentUserId, secondOptionImpactParam.createdBy());

        verifyNoInteractions(
            updateQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactDeleted_DeleteFromDatabase() {
        var levelTwo = levelTwo();
        var levelThree = levelThree();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact1 = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var savedImpact2 = createQuestionImpact(attribute.getId(), levelThree.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        var savedOptionImpact3 = createAnswerOptionImpact(answerOption1.getId(), 0.5);
        var savedOptionImpact4 = createAnswerOptionImpact(answerOption2.getId(), 0.75);
        savedImpact1.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedImpact2.setOptionImpacts(List.of(savedOptionImpact3, savedOptionImpact4));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact1, savedImpact2));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo, levelThree), List.of(savedQuestionnaire));

        doNothing().when(deleteQuestionImpactPort).delete(savedImpact2.getId(), savedImpact2.getKitVersionId());

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo, levelThree).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, savedQuestionnaire.getId());
        var attribute1 = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute1.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute1))), List.of(levelTwo), List.of(savedQuestionnaire));


        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 2);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute1).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionImpactUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));

        doNothing().when(updateAnswerOptionImpactPort).update(any(UpdateAnswerOptionImpactPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 0.75D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, true, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OLD_OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));


        doNothing().when(updateAnswerOptionPort).update(any(UpdateAnswerOptionPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }


    @Test
    void testQuestionUpdateKitPersister_dslHasOneNewQuestion_SaveQuestionWithItsImpactsAndOptions() {
        var levelTwo = levelTwo();
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var subject = SubjectMother.subjectWithAttributes("subject1", List.of(attribute));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of());
        var savedKit = AssessmentKitMother.completeKit(List.of(subject), List.of(levelTwo), List.of(questionnaire));

        var question = QuestionMother.createQuestion(QUESTION_CODE2, QUESTION_TITLE2, 2, "", Boolean.FALSE, Boolean.TRUE, 1L);
        var impact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, question.getId());
        var answerOption1 = createAnswerOption(question.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(question.getId(), OPTION_TITLE, OPTION_INDEX2);
        var optionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var optionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        impact.setOptionImpacts(List.of(optionImpact1, optionImpact2));
        question.setOptions(List.of(answerOption1, answerOption2));
        question.setImpacts(List.of(impact));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslSubject = SubjectDslModelMother.domainToDslModel(subject, b -> b.questionnaireCodes(List.of(questionnaire.getCode())));

        var dslQuestion = QuestionDslModelMother.domainToDslModel(question, q -> q
            .questionImpacts(List.of(dslImpact))
            .answerOptions(dslAnswerOptionList)
            .questionnaireCode(questionnaire.getCode()));

        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestion))
            .subjects(List.of(dslSubject))
            .build();

        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any(), eq(savedKit.getActiveVersionId()))).thenReturn(List.of(answerOption1, answerOption2));

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(questionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        ctx.put(KEY_SUBJECTS, Stream.of(subject).collect(toMap(Subject::getCode, Subject::getId)));
        persister.persist(ctx, savedKit, dslKit, UUID.randomUUID());

        verifyNoInteractions(
            updateQuestionPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );

    }
}
