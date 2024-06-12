package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.allLevels;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttributeValueTest {

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

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withQuestions(questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFive().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculate_onlyImpactOnLevels23() {
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withQuestions(questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelThree().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculate_onlyImpactOnLevels45() {
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withQuestions(questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFive().getValue(), qav.getMaturityLevel().getValue());
        assertEquals(allLevels().size(), qav.getMaturityScores().size());

        List<MaturityScore> matchingScores = qav.getMaturityScores().stream()
            .filter(score -> allLevels().stream()
                .anyMatch(level -> level.getId() == score.getMaturityLevelId()))
            .toList();
        assertEquals(allLevels().size(), matchingScores.size());
    }

    @Test
    void testCalculate_onlyImpactOnLevels24() {
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withQuestions(questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFour().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculate_withQuestionImpactfulOnLevel24AndMarkedAsNotApplicable() {
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withQuestions(questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFour().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculateConfidenceLevel_fullScore() {
        Question q1 = QuestionMother.withImpactsOnLevel23();
        Question q2 = QuestionMother.withImpactsOnLevel23();
        Question q3 = QuestionMother.withImpactsOnLevel34();
        Question q4 = QuestionMother.withImpactsOnLevel45();
        Question q5 = QuestionMother.withImpactsOnLevel45();
        List<Question> questions = List.of(q1, q2, q3, q4, q5);

        List<Answer> answers = List.of(
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q1.getId()),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q2.getId()),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q3.getId()),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q4.getId()),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q5.getId()));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withQuestions(questions), answers);

        qav.calculateConfidenceValue();

        assertEquals(100.0, qav.getConfidenceValue());
    }
}
