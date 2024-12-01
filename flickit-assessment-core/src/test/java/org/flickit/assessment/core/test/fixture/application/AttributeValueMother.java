package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;
import java.util.UUID;

public class AttributeValueMother {

    public static AttributeValue toBeCalcWithAttributeAndAnswers(Attribute attribute, List<Answer> answers) {
        return new AttributeValue(UUID.randomUUID(), attribute, answers);
    }

    public static AttributeValue hasFullScoreOnLevel23WithWeight(int weight, long attributeId) {
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

        return new AttributeValue(UUID.randomUUID(),
            AttributeMother.withIdQuestionsAndWeight(attributeId, questions, weight),
            answers);
    }

    public static AttributeValue hasPartialScoreOnLevel2AndFullScoreOnLevel3WithWeight(int weight, long attributeId) {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel23(attributeId),
            QuestionMother.withImpactsOnLevel45(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.partialScoreOnLevel2AndFullScoreOnLevel3(attributeId),
            AnswerMother.partialScoreOnLevel2AndFullScoreOnLevel3(attributeId),
            AnswerMother.partialScoreOnLevel2AndFullScoreOnLevel3(attributeId),
            AnswerMother.partialScoreOnLevel2AndFullScoreOnLevel3(attributeId),
            AnswerMother.partialScoreOnLevel2AndFullScoreOnLevel3(attributeId),
            AnswerMother.noScoreOnLevel4(attributeId));

        return new AttributeValue(UUID.randomUUID(),
            AttributeMother.withIdQuestionsAndWeight(attributeId, questions, weight),
            answers);
    }

    public static AttributeValue hasFullScoreOnLevel24WithWeight(int weight, long attributeId) {
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


        return new AttributeValue(UUID.randomUUID(),
            AttributeMother.withIdQuestionsAndWeight(attributeId, questions, weight),
            answers);
    }

    public static AttributeValue toBeCalcAsConfidenceLevelWithWeight(int weight, int confidenceLevelId) {
        long attributeId = 1533;
        Question q1 = QuestionMother.withImpactsOnLevel24(attributeId);
        Question q2 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q3 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q4 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q5 = QuestionMother.withImpactsOnLevel23(attributeId);
        Question q6 = QuestionMother.withImpactsOnLevel45(attributeId);
        List<Question> questions = List.of(q1, q2, q3, q4, q5, q6);

        List<Answer> answers = List.of(
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q1.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q2.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q3.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q4.getId(), attributeId),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q5.getId(), attributeId));

        return new AttributeValue(UUID.randomUUID(),
            AttributeMother.withIdQuestionsAndWeight(attributeId, questions, weight),
            answers);
    }

    public static AttributeValue withAttributeAndAnswerAndLevelOne(Attribute attribute, List<Answer> answers) {
        return new AttributeValue(UUID.randomUUID(), attribute, answers, null, MaturityLevelMother.levelOne(), null);
    }
}
