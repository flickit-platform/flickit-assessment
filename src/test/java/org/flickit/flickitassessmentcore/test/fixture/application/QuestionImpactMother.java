package org.flickit.flickitassessmentcore.test.fixture.application;

import org.flickit.flickitassessmentcore.application.domain.QuestionImpact;

import static org.flickit.flickitassessmentcore.test.fixture.application.MaturityLevelMother.*;

public class QuestionImpactMother {

    private static long id = 134L;

    public static QuestionImpact onLevelOne(int weight) {
        return new QuestionImpact(id++, weight, levelOne().getId());
    }

    public static QuestionImpact onLevelTwo(int weight) {
        return new QuestionImpact(id++, weight, levelTwo().getId());
    }

    public static QuestionImpact onLevelThree(int weight) {
        return new QuestionImpact(id++, weight, levelThree().getId());
    }

    public static QuestionImpact onLevelFour(int weight) {
        return new QuestionImpact(id++, weight, levelFour().getId());
    }

    public static QuestionImpact onLevelFive(int weight) {
        return new QuestionImpact(id++, weight, levelFive().getId());
    }
}
