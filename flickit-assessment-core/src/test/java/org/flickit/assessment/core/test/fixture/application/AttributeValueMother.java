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
            AnswerMother.fullScore(questions.get(0).getId()),
            AnswerMother.fullScore(questions.get(1).getId()),
            AnswerMother.fullScore(questions.get(2).getId()),
            AnswerMother.fullScore(questions.get(3).getId()),
            AnswerMother.fullScore(questions.get(4).getId()),
            AnswerMother.noScore(questions.get(5).getId()));

        return new AttributeValue(UUID.randomUUID(),
            AttributeMother.withIdQuestionsAndWeight(attributeId, questions, weight),
            answers);
    }

    public static AttributeValue hasPartialScoreOnLevel2AndFullScoreOnLevel3WithWeight(int weight, long attributeId) {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel2(attributeId),
            QuestionMother.withImpactsOnLevel2(attributeId),
            QuestionMother.withImpactsOnLevel2(attributeId),
            QuestionMother.withImpactsOnLevel2(attributeId),
            QuestionMother.withImpactsOnLevel2(attributeId),
            QuestionMother.withImpactsOnLevel2(attributeId),
            QuestionMother.withImpactsOnLevel3(attributeId),
            QuestionMother.withImpactsOnLevel3(attributeId),
            QuestionMother.withImpactsOnLevel3(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.partialScore(questions.get(0).getId(), 0.7),
            AnswerMother.partialScore(questions.get(1).getId(), 0.7),
            AnswerMother.partialScore(questions.get(2).getId(), 0.7),
            AnswerMother.partialScore(questions.get(3).getId(), 0.7),
            AnswerMother.partialScore(questions.get(4).getId(), 0.7),
            AnswerMother.partialScore(questions.get(5).getId(), 0.7),
            AnswerMother.fullScore(questions.get(6).getId()),
            AnswerMother.fullScore(questions.get(7).getId()),
            AnswerMother.fullScore(questions.get(8).getId()));

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
            QuestionMother.withImpactsOnLevel24(attributeId));

        List<Answer> answers = List.of(
            AnswerMother.fullScore(questions.get(0).getId()),
            AnswerMother.fullScore(questions.get(1).getId()),
            AnswerMother.fullScore(questions.get(2).getId()),
            AnswerMother.fullScore(questions.get(3).getId()),
            AnswerMother.fullScore(questions.get(4).getId()),
            AnswerMother.fullScore(questions.get(5).getId()));


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
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q1.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q2.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q3.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q4.getId()),
            AnswerMother.answerWithConfidenceLevel(confidenceLevelId, q5.getId()));

        return new AttributeValue(UUID.randomUUID(),
            AttributeMother.withIdQuestionsAndWeight(attributeId, questions, weight),
            answers);
    }

    public static AttributeValue withAttributeAndAnswerAndLevelOne(Attribute attribute, List<Answer> answers) {
        return new AttributeValue(UUID.randomUUID(), attribute, answers, null, MaturityLevelMother.levelOne(), null);
    }
}
