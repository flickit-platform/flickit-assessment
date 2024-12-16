package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl.DslFieldNames.ANSWER_OPTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl.DslFieldNames.QUESTION;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createAnswerRangeWithNoOptions;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithQuestionnaires;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionUpdateKitValidatorTest {

    @InjectMocks
    private QuestionUpdateKitValidator validator;

    @Mock
    private LoadAnswerRangePort loadAnswerRangePort;

    @Test
    void testValidator_SameQuestionsInDbAndDsl_Valid() {
        var questionOne = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 153L, 1L);
        var questionTwo = createQuestion(QUESTION_CODE2, QUESTION_TITLE2, 2, "", Boolean.FALSE, Boolean.TRUE, 153L, 1L);
        questionOne.setOptions(List.of());
        questionTwo.setOptions(List.of());
        var questionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne, questionTwo));
        var savedKit = kitWithQuestionnaires(List.of(questionnaire));
        var answerRange = createAnswerRangeWithNoOptions(questionOne.getAnswerRangeId(), false);

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

        when(loadAnswerRangePort.load(questionOne.getAnswerRangeId(), savedKit.getActiveVersionId())).thenReturn(answerRange);

        Notification notification = validator.validate(savedKit, dslKit);

        assertFalse(notification.hasErrors());
    }

    @Test
    void testValidator_dslHasOneQuestionLessThanDb_Invalid() {
        var questionOne = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 153L, 1L);
        var questionTwo = createQuestion(QUESTION_CODE2, QUESTION_TITLE2, 2, "", Boolean.FALSE, Boolean.TRUE, 153L, 1L);
        questionOne.setOptions(List.of());
        questionTwo.setOptions(List.of());
        var questionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne, questionTwo));
        var savedKit = kitWithQuestionnaires(List.of(questionnaire));
        var answerRange = createAnswerRangeWithNoOptions(questionOne.getAnswerRangeId(), false);

        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of())
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne))
            .build();

        when(loadAnswerRangePort.load(questionOne.getAnswerRangeId(), savedKit.getActiveVersionId())).thenReturn(answerRange);

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
        var questionOne = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 153L, 1L);
        questionOne.setOptions(List.of());
        var questionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne));
        var savedKit = kitWithQuestionnaires(List.of(questionnaire));
        var answerRange = createAnswerRangeWithNoOptions(questionOne.getAnswerRangeId(), false);

        var newAnswerOption = AnswerOptionDslModel.builder()
            .caption(OPTION_TITLE)
            .index(1)
            .value(1D)
            .build();
        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of(newAnswerOption))
            .questionnaireCode(questionnaire.getCode())
            .answerRangeCode(answerRange.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne))
            .build();

        when(loadAnswerRangePort.load(questionOne.getAnswerRangeId(), savedKit.getActiveVersionId())).thenReturn(answerRange);

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
    void testValidator_dslHasOneAnswerOptionLessThanDb_Invalid() {
        var questionOne = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, "", Boolean.FALSE, Boolean.TRUE, 153L, 1L);
        var deletedAnswerOption = AnswerOptionMother.createAnswerOption(questionOne.getAnswerRangeId(), OPTION_TITLE, 1);
        questionOne.setOptions(List.of(deletedAnswerOption));
        var questionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        questionnaire.setQuestions(List.of(questionOne));
        var savedKit = kitWithQuestionnaires(List.of(questionnaire));
        var answerRange = createAnswerRangeWithNoOptions(questionOne.getAnswerRangeId(), false);

        var dslQuestionOne = QuestionDslModelMother.domainToDslModel(questionOne, q -> q
            .answerOptions(List.of())
            .questionnaireCode(questionnaire.getCode()));
        var dslQuestionnaires = QuestionnaireDslModelMother.domainToDslModel(questionnaire);
        var dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaires))
            .questions(List.of(dslQuestionOne))
            .build();

        when(loadAnswerRangePort.load(questionOne.getAnswerRangeId(), savedKit.getActiveVersionId())).thenReturn(answerRange);

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
