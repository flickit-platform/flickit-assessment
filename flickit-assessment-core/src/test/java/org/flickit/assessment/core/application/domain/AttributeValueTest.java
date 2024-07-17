package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.allLevels;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttributeValueTest {

    @Test
    void testCalculate_fullScoreOnAllLevels() {
        long attributeId = 256L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel34(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.fullScoreOnLevels34(attributeId),
            AnswerMother.fullScoreOnLevels45(attributeId),
            AnswerMother.fullScoreOnLevels45(attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFive().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculate_onlyImpactOnLevels23() {
        long attributeId = 256L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.fullScoreOnLevels23(attributeId),
            AnswerMother.noScoreOnLevel4(attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelThree().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculate_onlyImpactOnLevels45() {
        long attributeId = 256L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel45(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels45(attributeId),
            AnswerMother.fullScoreOnLevels45(attributeId),
            AnswerMother.fullScoreOnLevels45(attributeId),
            AnswerMother.fullScoreOnLevels45(attributeId),
            AnswerMother.fullScoreOnLevels45(attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

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
        long attributeId = 256L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevel4AndNoScoreOnLevel5(attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFour().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculate_withQuestionImpactfulOnLevel24AndMarkedAsNotApplicable() {
        long attributeId = 256L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel24(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(0).getId()),
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(1).getId()),
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(2).getId()),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevel4AndNoScoreOnLevel5(attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        assertEquals(MaturityLevelMother.levelFour().getValue(), qav.getMaturityLevel().getValue());
    }

    @Test
    void testCalculateConfidenceLevel_fullScore() {
        long attributeId = 256L;
        Question q1 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q2 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q3 = QuestionMother.withImpactsOnLevel34(attributeId);
        Question q4 = QuestionMother.withImpactsOnLevel45(attributeId);
        Question q5 = QuestionMother.withImpactsOnLevel45(attributeId);
        List<Question> questions = List.of(q1, q2, q3, q4, q5);

        List<Answer> answers = List.of(
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q1.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q2.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q3.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q4.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q5.getId(), attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithQAAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculateConfidenceValue();

        assertEquals(100.0, qav.getConfidenceValue());
    }
}
