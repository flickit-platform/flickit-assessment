package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionByIndexPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.DeleteAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelByCodePort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributeByCodePort;
import org.flickit.assessment.kit.application.port.out.qualityattribute.LoadQualityAttributePort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionByCodePort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByAttributeAndMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.flickit.assessment.kit.test.fixture.application.dsl.model.mother.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionUpdateKitPersisterTest {

    @InjectMocks
    private QuestionUpdateKitPersister persister;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private LoadMaturityLevelPort loadMaturityLevelPort;

    @Mock
    private LoadQualityAttributePort loadQualityAttributePort;

    @Mock
    private LoadMaturityLevelByCodePort loadMaturityLevelByCodePort;

    @Mock
    private LoadQualityAttributeByCodePort loadQualityAttributeByCodePort;

    @Mock
    private LoadQuestionByCodePort loadQuestionByCodePort;

    @Mock
    private CreateQuestionImpactPort createQuestionImpactPort;

    @Mock
    private DeleteQuestionImpactPort deleteQuestionImpactPort;

    @Mock
    private UpdateQuestionImpactPort updateQuestionImpactPort;

    @Mock
    private CreateAnswerOptionImpactPort createAnswerOptionImpactPort;

    @Mock
    private LoadAnswerOptionByIndexPort loadAnswerOptionByIndexPort;

    @Mock
    private DeleteAnswerOptionImpactPort deleteAnswerOptionImpactPort;

    @Mock
    private UpdateAnswerOptionImpactPort updateAnswerOptionImpactPort;

    @Mock
    private LoadQuestionImpactByAttributeAndMaturityLevelPort loadQuestionImpactByAttributeAndMaturityLevelPort;

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        Long kitId = 1L;
        AssessmentKit savedKit = buildQuestionsAndMock(QUESTION_TITLE1, false, kitId);
        AssessmentKitDslModel dslKit = buildAssessmentKitDslModel();

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            deleteAnswerOptionImpactPort,
            updateAnswerOptionImpactPort
        );
    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_QuestionUpdated_UpdateInDatabase() {
        Long kitId = 1L;
        AssessmentKit savedKit = buildQuestionsAndMock(QUESTION_OLD_TITLE1, false, kitId);
        doNothing().when(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));
        AssessmentKitDslModel dslKit = buildAssessmentKitDslModel();

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            deleteAnswerOptionImpactPort,
            updateAnswerOptionImpactPort
        );
    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_QuestionImpactAdded_AddToDatabase() {
        Long kitId = 1L;

        AssessmentKit savedKit = buildQuestionsAndMock(QUESTION_TITLE1, true, kitId);
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(1L);
        when(createAnswerOptionImpactPort.persist(any(CreateAnswerOptionImpactPort.Param.class))).thenReturn(1L);
        AssessmentKitDslModel dslKit = buildAssessmentKitDslModel();

        persister.persist(savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionPort,
            deleteAnswerOptionImpactPort,
            updateAnswerOptionImpactPort
        );
    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_QuestionImpactDeleted_DeleteFromDatabase() {

    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_QuestionImpactUpdated_UpdateInDatabase() {

    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_AnswerOptionImpactAdded_AddToDatabase() {

    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_AnswerOptionImpactDeleted_DeleteFromDatabase() {

    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_AnswerOptionImpactUpdated_UpdateInDatabase() {

    }

    private AssessmentKit buildQuestionsAndMock(String updateQuestionTitle1, Boolean removeAnImpact, Long kitId) {
        var levelTwo = MaturityLevelMother.levelTwo();
        var levelThree = MaturityLevelMother.levelThree();

        var savedQuestionnaire1 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE1, QUESTIONNAIRE_TITLE1, 1);
        var savedQuestionnaire2 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE2, QUESTIONNAIRE_TITLE2, 2);

        var savedQuestion11 = QuestionMother.createQuestion(QUESTION_CODE1, updateQuestionTitle1, 1, null, false, savedQuestionnaire1.getId());
        var savedQuestion21 = QuestionMother.createQuestion(QUESTION_CODE2, QUESTION_TITLE2, 2, null, false, savedQuestionnaire2.getId());

        var attribute1 = AttributeMother.createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var attribute2 = AttributeMother.createAttribute(ATTRIBUTE_CODE2, ATTRIBUTE_TITLE2, 2, "", 1);

        var savedImpact111 = QuestionImpactMother.createQuestionImpact(attribute1.getId(), levelTwo.getId(), 1, savedQuestion11.getId());
        var savedImpact211 = QuestionImpactMother.createQuestionImpact(attribute2.getId(), levelThree.getId(), 1, savedQuestion21.getId());
        var savedImpact212 = QuestionImpactMother.createQuestionImpact(attribute1.getId(), levelTwo.getId(), 1, savedQuestion21.getId());
        if (removeAnImpact) savedImpact212 = null;

        AnswerOption answerOption11 = AnswerOptionMother.createAnswerOption(savedQuestion11.getId(), OPTION_TITLE, OPTION_INDEX1);
        AnswerOption answerOption12 = AnswerOptionMother.createAnswerOption(savedQuestion11.getId(), OPTION_TITLE, OPTION_INDEX2);
        AnswerOption answerOption13 = AnswerOptionMother.createAnswerOption(savedQuestion11.getId(), OPTION_TITLE, OPTION_INDEX3);
        AnswerOption answerOption14 = AnswerOptionMother.createAnswerOption(savedQuestion11.getId(), OPTION_TITLE, OPTION_INDEX4);
        AnswerOption answerOption21 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, OPTION_INDEX1);
        AnswerOption answerOption22 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, OPTION_INDEX2);
        AnswerOption answerOption23 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, OPTION_INDEX3);
        AnswerOption answerOption24 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, OPTION_INDEX4);

        var savedOptionImpact1111 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption11.getId(), 0);
        var savedOptionImpact1112 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption12.getId(), 0);
        var savedOptionImpact1113 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption13.getId(), 0.5);
        var savedOptionImpact1114 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption14.getId(), 1);
        var savedOptionImpact2111 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption21.getId(), 0);
        var savedOptionImpact2112 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption22.getId(), 0);
        var savedOptionImpact2113 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption23.getId(), 0.5);
        var savedOptionImpact2114 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption24.getId(), 1);

        savedImpact111.setOptionImpacts(List.of(savedOptionImpact1111, savedOptionImpact1112, savedOptionImpact1113, savedOptionImpact1114));
        savedImpact211.setOptionImpacts(List.of(savedOptionImpact2111, savedOptionImpact2112, savedOptionImpact2113, savedOptionImpact2114));
        if (savedImpact212 != null) {
            savedImpact212.setOptionImpacts(List.of(savedOptionImpact2111, savedOptionImpact2112, savedOptionImpact2113, savedOptionImpact2114));
        }

        savedQuestion11.setImpacts(List.of(savedImpact111));
        savedQuestion21.setImpacts(Stream.of(savedImpact211, savedImpact212).filter(Objects::nonNull).toList());

        savedQuestionnaire1.setQuestions(List.of(savedQuestion11));
        savedQuestionnaire2.setQuestions(List.of(savedQuestion21));

        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1, savedQuestionnaire2), kitId);

        List<Attribute> attributes = List.of(attribute1, attribute2);
        List<MaturityLevel> maturityLevels = List.of(levelTwo, levelThree);
        List<QuestionImpact> impacts = Stream.of(savedImpact111, savedImpact211, savedImpact212).filter(Objects::nonNull).toList();
        List<AnswerOption> answerOptions = List.of(answerOption11, answerOption12, answerOption13, answerOption14, answerOption21, answerOption22, answerOption23, answerOption24);
        List<Question> questions = List.of(savedQuestion11, savedQuestion21);

        attributes.forEach(a -> when(loadQualityAttributePort.load(a.getId())).thenReturn(Optional.of(a)));
        attributes.forEach(a -> when(loadQualityAttributeByCodePort.loadByCode(a.getCode())).thenReturn(a));
        maturityLevels.forEach(l -> when(loadMaturityLevelPort.load(l.getId())).thenReturn(Optional.of(l)));
        maturityLevels.forEach(l -> when(loadMaturityLevelByCodePort.loadByCode(l.getCode(), kitId)).thenReturn(l));
        impacts.forEach(s -> when(loadQuestionImpactByAttributeAndMaturityLevelPort.loadByAttributeCodeAndMaturityLevelCode(s.getAttributeId(), s.getMaturityLevelId())).thenReturn(s));
        answerOptions.forEach(a -> when(loadAnswerOptionByIndexPort.loadByIndex(a.getIndex(), a.getQuestionId())).thenReturn(a));
        questions.forEach(q -> when(loadQuestionByCodePort.loadByCode(q.getCode())).thenReturn(q));

        return savedKit;
    }

    private AssessmentKitDslModel buildAssessmentKitDslModel() {
        var dslQuestionnaire1 = QuestionnaireDslModelMother.questionnaireDslModel(QUESTIONNAIRE_CODE1, 1, QUESTIONNAIRE_TITLE1, "");
        var dslQuestionnaire2 = QuestionnaireDslModelMother.questionnaireDslModel(QUESTIONNAIRE_CODE2, 2, QUESTIONNAIRE_TITLE2, "");

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.maturityLevelDslModel(LEVEL_TWO_CODE, 2, LEVEL_TWO_CODE, "", null, 2);
        var dslMaturityLevelThree = MaturityLevelDslModelMother.maturityLevelDslModel(LEVEL_THREE_CODE, 3, LEVEL_THREE_CODE, "", null, 3);

        var dslAnswerOption1 = AnswerOptionDslModelMother.answerOptionDslModel(1, "Weak", 1);
        var dslAnswerOption2 = AnswerOptionDslModelMother.answerOptionDslModel(2, "Moderate", 1);
        var dslAnswerOption3 = AnswerOptionDslModelMother.answerOptionDslModel(3, "Good", 1);
        var dslAnswerOption4 = AnswerOptionDslModelMother.answerOptionDslModel(4, "Great", 1);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2, dslAnswerOption3, dslAnswerOption4);

        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption3.getIndex(), 0.5D);
        optionsIndexToValueMap.put(dslAnswerOption4.getIndex(), 1D);

        var dslImpact1 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslImpact2 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE2, dslMaturityLevelThree, null, optionsIndexToValueMap, 1);
        var dslImpact3 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);

        var dslQuestion1 = QuestionDslModelMother.questionDslModel(
            QUESTION_CODE1, 1, QUESTION_TITLE1, null, QUESTIONNAIRE_CODE1, List.of(dslImpact1), dslAnswerOptionList, Boolean.FALSE);
        var dslQuestion2 = QuestionDslModelMother.questionDslModel(
            QUESTION_CODE2, 2, QUESTION_TITLE2, null, QUESTIONNAIRE_CODE2, List.of(dslImpact2, dslImpact3), dslAnswerOptionList, Boolean.FALSE);

        dslImpact1.setQuestion(dslQuestion1);
        dslImpact2.setQuestion(dslQuestion2);
        dslImpact3.setQuestion(dslQuestion2);

        return AssessmentKitDslModelMother.assessmentKitDslModel(
            List.of(dslQuestionnaire1, dslQuestionnaire2), null, List.of(dslQuestion1, dslQuestion2), null, null);
    }
}
