package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.toBeCalcWithAttributeAndAnswers;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.allLevels;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelFive;
import static org.flickit.assessment.core.test.fixture.application.QuestionMother.withImpactsOnLevel45;
import static org.junit.jupiter.api.Assertions.*;

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

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        assertEquals(levelFive().getValue(), qav.getMaturityLevel().getValue());
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        assertEquals(levelFive().getValue(), qav.getMaturityLevel().getValue());
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
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
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.getFirst().getId()),
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(1).getId()),
            AnswerMother.answerWithQuestionIdAndNotApplicableTrue(questions.get(2).getId()),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevels24(attributeId),
            AnswerMother.fullScoreOnLevel4AndNoScoreOnLevel5(attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
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

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculateConfidenceValue();

        assertEquals(100.0, qav.getConfidenceValue());
    }

    @Test
    void testCalculateConfidenceLevel_whenSomeQuestionsHaveNoAnswer() {
        long attributeId = 256L;
        Question q1 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q2 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q3 = QuestionMother.withImpactsOnLevel34(attributeId);
        Question q4 = withImpactsOnLevel45(attributeId);
        Question q5 = withImpactsOnLevel45(attributeId);
        List<Question> questions = List.of(q1, q2, q3, q4, q5); //all questions have weight = 1;

        List<Answer> answers = List.of(
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.COMPLETELY_SURE.getId(), q1.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.SOMEWHAT_UNSURE.getId(), q2.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(ConfidenceLevel.FAIRLY_SURE.getId(), q3.getId(), attributeId));

        AttributeValue qav = toBeCalcWithAttributeAndAnswers(AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculateConfidenceValue();

        double maxPossibleSumConfidence = 25;
        double gainedSumConfidence = 5 + 3 + 4;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;

        assertEquals(confidenceValue, qav.getConfidenceValue());
    }

    @Test
    void testCalculateConfidenceLevel_whitNoAnsweredQuestions() {
        long attributeId = 256L;
        Question q1 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q2 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q3 = QuestionMother.withImpactsOnLevel34(attributeId);
        Question q4 = withImpactsOnLevel45(attributeId);
        Question q5 = withImpactsOnLevel45(attributeId);
        List<Question> questions = List.of(q1, q2, q3, q4, q5); //all questions have weight = 1;

        List<Answer> answers = List.of();

        AttributeValue qav = toBeCalcWithAttributeAndAnswers(AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculateConfidenceValue();

        double maxPossibleSumConfidence = 25;
        double gainedSumConfidence = 0;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;

        assertEquals(confidenceValue, qav.getConfidenceValue());
    }

    @Test
    void testCalculate_onlyImpactOnLevel3_totalScoreOnLevel4ShouldBeNull() {
        long attributeId = 256L;
        long anotherAttributeId = 257L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId));

        List<Answer> answers = List.of();

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        Map<Long, Double> totalScore = ReflectionTestUtils.invokeMethod(qav, "calcTotalScore", allLevels());
        assertNotNull(totalScore);
        Double attributeTotalImpactOnLevelFour = totalScore.get(MaturityLevelMother.LEVEL_FOUR_ID);
        assertNull(attributeTotalImpactOnLevelFour);

        Double attributeTotalImpactOnLevelTree = totalScore.get(MaturityLevelMother.LEVEL_THREE_ID);
        assertEquals(1, attributeTotalImpactOnLevelTree);
    }

    @Test
    void testCalculate_onlyImpactOnLevel34_totalScoreOnLevel34ShouldBeCorrect() {
        long attributeId = 256L;
        long anotherAttributeId = 257L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId),
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, attributeId));

        List<Answer> answers = List.of();

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        Map<Long, Double> totalScore = ReflectionTestUtils.invokeMethod(qav, "calcTotalScore", allLevels());
        assertNotNull(totalScore);
        Double attributeTotalImpactOnLevelFour = totalScore.get(MaturityLevelMother.LEVEL_FOUR_ID);
        assertEquals(1, attributeTotalImpactOnLevelFour);

        Double attributeTotalImpactOnLevelTree = totalScore.get(MaturityLevelMother.LEVEL_THREE_ID);
        assertEquals(2, attributeTotalImpactOnLevelTree);
    }

    @Test
    void testCalculate_onlyImpactOnLevel3_gainedScoreOnLevel4ShouldBeNull() {
        long attributeId = 256L;
        long anotherAttributeId = 257L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        Map<Long, Double> gainedScore = ReflectionTestUtils.invokeMethod(qav, "calcGainedScore", allLevels());
        assertNotNull(gainedScore);

        Double attributeGainedScoreOnLevel4 = gainedScore.get(MaturityLevelMother.LEVEL_FOUR_ID);
        assertNull(attributeGainedScoreOnLevel4);

        Double attributeGainedScoreOnLevel3 = gainedScore.get(MaturityLevelMother.LEVEL_THREE_ID);
        assertEquals(1, attributeGainedScoreOnLevel3);
    }

    @Test
    void testCalculate_onlyImpactOnLevels34_gainedScoreOnLevel34ShouldBeCorrect() {
        long attributeId = 256L;
        long anotherAttributeId = 257L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId),
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, attributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId),
            AnswerMother.fullScoreOnLevel3AndAnotherAttributeLevel4(attributeId, attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        Map<Long, Double> gainedScore = ReflectionTestUtils.invokeMethod(qav, "calcGainedScore", allLevels());
        assertNotNull(gainedScore);

        Double attributeGainedScoreOnLevel4 = gainedScore.get(MaturityLevelMother.LEVEL_FOUR_ID);
        assertEquals(1, attributeGainedScoreOnLevel4);

        Double attributeGainedScoreOnLevel3 = gainedScore.get(MaturityLevelMother.LEVEL_THREE_ID);
        assertEquals(2, attributeGainedScoreOnLevel3);
    }

    @Test
    void testCalculate_onlyImpactOnLevel3_maturityScoreOnLevel4ShouldBeNull() {
        long attributeId = 256L;
        long anotherAttributeId = 257L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId));

        List<Answer> answers = List.of();

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        Set<MaturityScore> maturityScores = qav.getMaturityScores();

        var maturityScoreOnLevel4 = maturityScores.stream()
            .filter(m -> Objects.equals(m.getMaturityLevelId(), MaturityLevelMother.LEVEL_FOUR_ID))
            .findAny();
        assertTrue(maturityScoreOnLevel4.isPresent());
        assertNull(maturityScoreOnLevel4.get().getScore());

        var maturityScoreOnLevel3 = maturityScores.stream()
            .filter(m -> Objects.equals(m.getMaturityLevelId(), MaturityLevelMother.LEVEL_THREE_ID))
            .findAny();
        assertTrue(maturityScoreOnLevel3.isPresent());
        assertNotNull(maturityScoreOnLevel3.get().getScore());
    }

    @Test
    void testCalculate_onlyImpactOnLevels34_maturityScoreOnLevel34ShouldBeCorrect() {
        long attributeId = 256L;
        long anotherAttributeId = 257L;
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, anotherAttributeId),
            QuestionMother.withImpactsOnLevel3AndAnotherAttributeLevel4(attributeId, attributeId));

        List<Answer> answers = List.of(
            AnswerMother.noScoreOnLevel3AndFullScoreOnAnotherAttributeLevel4(attributeId, anotherAttributeId),
            AnswerMother.fullScoreOnLevel3AndAnotherAttributeLevel4(attributeId, attributeId));

        AttributeValue qav = AttributeValueMother.toBeCalcWithAttributeAndAnswers(
            AttributeMother.withIdAndQuestions(attributeId, questions), answers);

        qav.calculate(allLevels());

        Set<MaturityScore> maturityScores = qav.getMaturityScores();

        var maturityScoreOnLevel4 = maturityScores.stream()
            .filter(m -> Objects.equals(m.getMaturityLevelId(), MaturityLevelMother.LEVEL_FOUR_ID))
            .findAny();

        assertTrue(maturityScoreOnLevel4.isPresent());
        assertNotNull(maturityScoreOnLevel4.get().getScore());
        assertEquals(100, maturityScoreOnLevel4.get().getScore());

        var maturityScoreOnLevel3 = maturityScores.stream()
            .filter(m -> Objects.equals(m.getMaturityLevelId(), MaturityLevelMother.LEVEL_THREE_ID))
            .findAny();
        assertTrue(maturityScoreOnLevel3.isPresent());
        assertNotNull(maturityScoreOnLevel3.get().getScore());
        assertEquals(50, maturityScoreOnLevel3.get().getScore());
    }
}
