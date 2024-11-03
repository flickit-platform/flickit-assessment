package org.flickit.assessment.kit.application.service.assessmentkit.update.validate.impl;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.validate.impl.DslFieldNames.ANSWER_OPTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.validate.impl.DslFieldNames.QUESTION;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class QuestionUpdateKitValidatorTest {

    @InjectMocks
    private QuestionUpdateKitValidator validator;

    @Test
    void testValidator_SameQuestionsInDbAndDsl_Valid() {
        var questionOne = QuestionMother.createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 1L);
        var questionTwo = QuestionMother.createQuestion(QUESTION_CODE2, QUESTION_TITLE2, 2, "", Boolean.FALSE, Boolean.TRUE, 1L);
        questionOne.setOptions(List.of());
        questionTwo.setOptions(List.of());
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne, questionTwo));
        var savedKit = AssessmentKitMother.kitWithQuestionnaires(List.of(questionnaire));

        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of())
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionTwo = QuestionDslModelMother.domainToDslModel(questionTwo, q -> q
            .answerOptions(List.of())
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne, dslQuestionTwo))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
    }

    @Test
    void testValidator_dslHasOneQuestionLessThanDb_Invalid() {
        var questionOne = QuestionMother.createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 1L);
        var questionTwo = QuestionMother.createQuestion(QUESTION_CODE2, QUESTION_TITLE2, 2, "", Boolean.FALSE, Boolean.TRUE, 1L);
        questionOne.setOptions(List.of());
        questionTwo.setOptions(List.of());
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne, questionTwo));
        var savedKit = AssessmentKitMother.kitWithQuestionnaires(List.of(questionnaire));

        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of())
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidDeletionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo(QUESTION);
                assertThat(x.deletedItems()).contains(questionTwo.getCode());
            });
    }

    @Test
    void testValidator_dslHasOneNewAnswerOption_Invalid() {
        var questionOne = QuestionMother.createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 1L);
        questionOne.setOptions(List.of());
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne));
        var savedKit = AssessmentKitMother.kitWithQuestionnaires(List.of(questionnaire));

        var newAnswerOption = AnswerOptionDslModel.builder()
            .caption(OPTION_TITLE)
            .index(1)
            .value(1D)
            .build();
        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of(newAnswerOption))
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidAdditionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo(ANSWER_OPTION);
                assertThat(x.addedItems()).contains(newAnswerOption.getCaption());
            });
    }

    @Test
    void testValidator_dslHasOneNewAnswerOptionLessThanDb_Invalid() {
        var questionOne = QuestionMother.createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 1L);
        var deletedAnswerOption = AnswerOptionMother.createAnswerOption(questionOne.getId(), OPTION_TITLE, 1);
        questionOne.setOptions(List.of(deletedAnswerOption));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne));
        var savedKit = AssessmentKitMother.kitWithQuestionnaires(List.of(questionnaire));

        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of())
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne))
            .build();

        Notification notification = validator.validate(savedKit, dslKit);

        assertThat(notification)
            .returns(true, Notification::hasErrors)
            .extracting(Notification::getErrors, as(COLLECTION))
            .singleElement()
            .isInstanceOfSatisfying(InvalidDeletionError.class, x -> {
                assertThat(x.fieldName()).isEqualTo(ANSWER_OPTION);
                assertThat(x.deletedItems()).contains(deletedAnswerOption.getTitle());
            });
    }
}
