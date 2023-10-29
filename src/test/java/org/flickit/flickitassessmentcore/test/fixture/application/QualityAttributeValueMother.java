package org.flickit.flickitassessmentcore.test.fixture.application;

import org.flickit.flickitassessmentcore.application.domain.*;

import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.test.fixture.application.MaturityScoreMother.maturityScoresOnAllLevels;

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
            maturityLevel);
    }
}
