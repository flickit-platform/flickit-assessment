package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;

public class QualityAttributeMother {

    private static long id = 134L;

    public static QualityAttribute simpleAttribute() {
        return new QualityAttribute(id++, 1, null);
    }

    public static QualityAttribute withQuestions(List<Question> questions) {
        return new QualityAttribute(id++, 1, questions);
    }

    public static QualityAttribute withQuestionsAndWeight(List<Question> questions, int weight) {
        return new QualityAttribute(id++, weight, questions);
    }

    public static QualityAttribute withQuestionsOnLevel23(int weight) {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23());

        return new QualityAttribute(id++, weight, questions);
    }

    public static QualityAttribute withQuestionsOnLevel24(int weight) {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24());

        return new QualityAttribute(id++, weight, questions);
    }

}
