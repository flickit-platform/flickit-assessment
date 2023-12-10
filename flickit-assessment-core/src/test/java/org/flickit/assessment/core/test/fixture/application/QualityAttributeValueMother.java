package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.*;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.application.MaturityScoreMother.maturityScoresOnAllLevels;

public class QualityAttributeValueMother {

    public static QualityAttributeValue toBeCalcWithQAAndAnswers(QualityAttribute qualityAttribute, List<Answer> answers) {
        return new QualityAttributeValue(UUID.randomUUID(), qualityAttribute, answers);
    }

    public static QualityAttributeValue toBeCalcAsLevelThreeWithWeight(int weight) {
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

        return new QualityAttributeValue(UUID.randomUUID(),
            QualityAttributeMother.withQuestionsAndWeight(questions, weight),
            answers);
    }

    public static QualityAttributeValue toBeCalcAsLevelFourWithWeight(int weight) {
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


        return new QualityAttributeValue(UUID.randomUUID(),
            QualityAttributeMother.withQuestionsAndWeight(questions, weight),
            answers);
    }

    public static QualityAttributeValue withAttributeAndMaturityLevel(QualityAttribute attribute, MaturityLevel maturityLevel) {
        return new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null,
            maturityScoresOnAllLevels(),
            maturityLevel,
            1.0);
    }

    public static QualityAttributeValue toBeCalcAsConfidenceLevelWithWeight(int weight, int confidenceLevelId) {
        Question q1 = QuestionMother.withImpactsOnLevel24();
        Question q2 = QuestionMother.withImpactsOnLevel23();
        Question q3 = QuestionMother.withImpactsOnLevel23();
        Question q4 = QuestionMother.withImpactsOnLevel23();
        Question q5 = QuestionMother.withImpactsOnLevel23();
        Question q6 = QuestionMother.withImpactsOnLevel45();
        List<Question> questions = List.of(q1, q2, q3, q4, q5, q6);

        List<Answer> answers = List.of(
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q1.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q2.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q3.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q4.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q5.getId()));

        return new QualityAttributeValue(UUID.randomUUID(),
            QualityAttributeMother.withQuestionsAndWeight(questions, weight),
            answers);
    }

}
