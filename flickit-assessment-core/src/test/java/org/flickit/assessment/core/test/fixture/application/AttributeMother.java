package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;

public class AttributeMother {

    private static long id = 134L;

    public static Attribute simpleAttribute() {
        return new Attribute(id++, 1, null);
    }

    public static Attribute withQuestions(List<Question> questions) {
        return new Attribute(id++, 1, questions);
    }

    public static Attribute withQuestionsAndWeight(List<Question> questions, int weight) {
        return new Attribute(id++, weight, questions);
    }

    public static Attribute withQuestionsOnLevel23(int weight) {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23(),
            QuestionMother.withImpactsOnLevel23());

        return new Attribute(id++, weight, questions);
    }

    public static Attribute withQuestionsOnLevel24(int weight) {
        List<Question> questions = List.of(
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24(),
            QuestionMother.withImpactsOnLevel24());

        return new Attribute(id++, weight, questions);
    }

}
