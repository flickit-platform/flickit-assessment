package org.flickit.flickitassessmentcore.application.domain;

import org.flickit.flickitassessmentcore.test.fixture.application.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QualityAttributeValueTest {

    @Test
    void testCalculate_fullScoreOnAllLevels() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel34(),
            QuestionMother.withImpactsOnLevel45(),
            QuestionMother.withImpactsOnLevel45());

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels34(),
            AnswerMother.fullScoreOnLevels45(),
            AnswerMother.fullScoreOnLevels45());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFive().getLevel(), qav.maturityLevel.getLevel());
    }

    @Test
    void calculate_onlyImpactOnLevels23() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel45());

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.noScoreOnLevel4());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getLevel(), qav.maturityLevel.getLevel());
    }

    @Test
    void calculate_onlyImpactOnLevels45() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel45(),
            QuestionMother.withImpactsOnLevel45(),
            QuestionMother.withImpactsOnLevel45(),
            QuestionMother.withImpactsOnLevel45(),
            QuestionMother.withImpactsOnLevel45());

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels45(),
            AnswerMother.fullScoreOnLevels45(),
            AnswerMother.fullScoreOnLevels45(),
            AnswerMother.fullScoreOnLevels45(),
            AnswerMother.fullScoreOnLevels45());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFive().getLevel(), qav.maturityLevel.getLevel());
    }

    @Test
    void calculate_onlyImpactOnLevels24() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel45());

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevel4AndNoScoreOnLevel5());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFour().getLevel(), qav.maturityLevel.getLevel());
    }

    @Test
    void calculate_withQuestionImpactfulOnLevel24AndMarkedAsNotApplicable() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel45());

        List<Answer> answers = List.of(
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(0).getId()),
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(1).getId()),
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(2).getId()),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevel4AndNoScoreOnLevel5());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFour().getLevel(), qav.maturityLevel.getLevel());
    }
}
