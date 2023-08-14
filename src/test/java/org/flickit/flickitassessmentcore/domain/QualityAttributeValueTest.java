package org.flickit.flickitassessmentcore.domain;

import org.flickit.flickitassessmentcore.domain.mother.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QualityAttributeValueTest {

    @Test
    void calculate_fullScoreOnAllLevels() {
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

        MaturityLevel maturityLevel = qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFive().getLevel(), maturityLevel.getLevel());
    }

    @Test
    void calculate_onlyImpactOnLevels23() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23());

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        MaturityLevel maturityLevel = qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getLevel(), maturityLevel.getLevel());
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

        MaturityLevel maturityLevel = qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFive().getLevel(), maturityLevel.getLevel());
    }

    @Test
    void calculate_onlyImpactOnLevels24() {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24());

        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24());

        QualityAttributeValue qav = QualityAttributeValueMother.toBeCalcWithQAAndAnswers(
            QualityAttributeMother.withQuestions(questions), answers);

        MaturityLevel maturityLevel = qav.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelFour().getLevel(), maturityLevel.getLevel());
    }
}
