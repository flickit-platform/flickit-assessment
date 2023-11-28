package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
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
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

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
        var levelTwo = MaturityLevelMother.levelTwo();
        var levelThree = MaturityLevelMother.levelThree();
        var levelFour = MaturityLevelMother.levelFour();
        var savedQuestionnaire1 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE1, QUESTIONNAIRE_TITLE1, 1);
        var savedQuestion11 = QuestionMother.createQuestion(
            QUESTION_CODE1,
            QUESTION_TITLE1,
            1,
            null,
            false,
            savedQuestionnaire1.getId());
        var attribute1 = AttributeMother.createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact111 = QuestionImpactMother.createQuestionImpact(attribute1.getId(), levelTwo.getId(), 1, savedQuestion11.getId());
        AnswerOption answerOption1 = AnswerOptionMother.createAnswerOption(savedQuestion11.getId(), OPTION_TITLE, 1);
        AnswerOption answerOption2 = AnswerOptionMother.createAnswerOption(savedQuestion11.getId(), OPTION_TITLE, 2);
        var savedOptionImpact1111 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact1112 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact111.setOptionImpacts(List.of(savedOptionImpact1111, savedOptionImpact1112));
        savedQuestion11.setImpacts(List.of(savedImpact111));
        savedQuestionnaire1.setQuestions(List.of(savedQuestion11));

        var savedQuestionnaire2 = QuestionnaireMother.questionnaire(QUESTIONNAIRE_CODE2, QUESTIONNAIRE_TITLE2, 2);
        var savedQuestion21 = QuestionMother.createQuestion(
            QUESTION_CODE2,
            QUESTION_TITLE2,
            2,
            null,
            true,
            savedQuestionnaire2.getId());
        var attribute2 = AttributeMother.createAttribute(ATTRIBUTE_CODE2, ATTRIBUTE_TITLE2, 2, "", 1);
        var savedImpact211 = QuestionImpactMother.createQuestionImpact(attribute2.getId(), levelTwo.getId(), 1, savedQuestion21.getId());
        var savedImpact212 = QuestionImpactMother.createQuestionImpact(attribute1.getId(), levelThree.getId(), 1, savedQuestion21.getId());
        var savedImpact213 = QuestionImpactMother.createQuestionImpact(attribute1.getId(), levelFour.getId(), 1, savedQuestion21.getId());
        AnswerOption answerOption3 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, 1);
        AnswerOption answerOption4 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, 2);
        AnswerOption answerOption5 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, 3);
        AnswerOption answerOption6 = AnswerOptionMother.createAnswerOption(savedQuestion21.getId(), OPTION_TITLE, 4);
        var savedOptionImpact2111 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption3.getId(), 0);
        var savedOptionImpact2112 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption4.getId(), 0);
        var savedOptionImpact2132 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption4.getId(), 0.5);
        var savedOptionImpact2113 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption5.getId(), 0.5);
        var savedOptionImpact2133 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption5.getId(), 1);
        var savedOptionImpact2114 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption6.getId(), 1);
        var savedOptionImpact2134 = AnswerOptionImpactMother.createAnswerOptionImpact(answerOption5.getId(), 0);
        savedImpact211.setOptionImpacts(List.of(savedOptionImpact2111, savedOptionImpact2112, savedOptionImpact2113, savedOptionImpact2114));
        savedImpact212.setOptionImpacts(List.of(savedOptionImpact2111, savedOptionImpact2132, savedOptionImpact2133, savedOptionImpact2114));
        savedImpact213.setOptionImpacts(List.of(savedOptionImpact2111, savedOptionImpact2112, savedOptionImpact2134, savedOptionImpact2114));
        savedQuestion21.setImpacts(List.of(savedImpact211, savedImpact212, savedImpact213));
        savedQuestionnaire2.setQuestions(List.of(savedQuestion21));


        AssessmentKit savedKit = AssessmentKitMother.kitWithQuestionnaire(List.of(savedQuestionnaire1, savedQuestionnaire2), kitId);

        when(loadQualityAttributePort.load(attribute1.getId())).thenReturn(Optional.of(attribute1));
        when(loadQualityAttributePort.load(attribute2.getId())).thenReturn(Optional.of(attribute2));
        when(loadMaturityLevelPort.load(levelTwo.getId())).thenReturn(Optional.of(levelTwo));
        when(loadMaturityLevelPort.load(levelThree.getId())).thenReturn(Optional.of(levelThree));
        when(loadMaturityLevelPort.load(levelFour.getId())).thenReturn(Optional.of(levelFour));
        when(loadQualityAttributeByCodePort.loadByCode(attribute1.getCode())).thenReturn(attribute1);
        when(loadQualityAttributeByCodePort.loadByCode(attribute2.getCode())).thenReturn(attribute2);
        when(loadMaturityLevelByCodePort.loadByCode(levelTwo.getCode(), kitId)).thenReturn(levelTwo);
        when(loadMaturityLevelByCodePort.loadByCode(levelThree.getCode(), kitId)).thenReturn(levelThree);
        when(loadMaturityLevelByCodePort.loadByCode(levelFour.getCode(), kitId)).thenReturn(levelFour);
        when(loadQuestionImpactByAttributeAndMaturityLevelPort.loadByAttributeCodeAndMaturityLevelCode(attribute1.getId(), levelTwo.getId())).thenReturn(savedImpact111);
        when(loadQuestionImpactByAttributeAndMaturityLevelPort.loadByAttributeCodeAndMaturityLevelCode(attribute2.getId(), levelTwo.getId())).thenReturn(savedImpact211);
        when(loadQuestionImpactByAttributeAndMaturityLevelPort.loadByAttributeCodeAndMaturityLevelCode(attribute1.getId(), levelThree.getId())).thenReturn(savedImpact212);
        when(loadQuestionImpactByAttributeAndMaturityLevelPort.loadByAttributeCodeAndMaturityLevelCode(attribute1.getId(), levelFour.getId())).thenReturn(savedImpact213);
        when(loadAnswerOptionByIndexPort.loadByIndex(1, savedQuestion11.getId())).thenReturn(answerOption1);
        when(loadAnswerOptionByIndexPort.loadByIndex(2, savedQuestion11.getId())).thenReturn(answerOption2);
        when(loadAnswerOptionByIndexPort.loadByIndex(1, savedQuestion21.getId())).thenReturn(answerOption3);
        when(loadAnswerOptionByIndexPort.loadByIndex(2, savedQuestion21.getId())).thenReturn(answerOption4);
        when(loadAnswerOptionByIndexPort.loadByIndex(3, savedQuestion21.getId())).thenReturn(answerOption5);
        when(loadAnswerOptionByIndexPort.loadByIndex(4, savedQuestion21.getId())).thenReturn(answerOption6);
        when(loadQuestionByCodePort.loadByCode(savedQuestion11.getCode())).thenReturn(savedQuestion11);
        when(loadQuestionByCodePort.loadByCode(savedQuestion21.getCode())).thenReturn(savedQuestion21);

        String dslContent = new String(Files.readAllBytes(Paths.get(FILE)));
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
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

    }

    @Test
    @SneakyThrows
    void testQuestionUpdateKitPersister_QuestionImpactAdded_AddToDatabase() {

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
}
