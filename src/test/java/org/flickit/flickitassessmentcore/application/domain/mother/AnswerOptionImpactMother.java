package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.AnswerOptionImpact;

public class AnswerOptionImpactMother {

    private static long id = 134L;

    public static AnswerOptionImpact onLevelTwo(double value) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelTwo(1));
    }

    public static AnswerOptionImpact onLevelThree(double value) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelThree(1));
    }

    public static AnswerOptionImpact onLevelFour(double value) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelFour(1));
    }

    public static AnswerOptionImpact onLevelFive(double value) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelFive(1));
    }
}
