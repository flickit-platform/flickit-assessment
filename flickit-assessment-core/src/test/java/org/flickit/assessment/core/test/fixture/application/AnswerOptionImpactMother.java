package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AnswerOptionImpact;

public class AnswerOptionImpactMother {

    private static long id = 134L;

    public static AnswerOptionImpact onLevelTwoOfAttributeId(double value, long attributeId) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelTwo(1, attributeId));
    }

    public static AnswerOptionImpact onLevelThreeOfAttributeId(double value, long attributeId) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelThree(1, attributeId));
    }

    public static AnswerOptionImpact onLevelFourOfAttributeId(double value, long attributeId) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelFour(1, attributeId));
    }

    public static AnswerOptionImpact onLevelFiveOfAttributeId(double value, long attributeId) {
        return new AnswerOptionImpact(id++, value, QuestionImpactMother.onLevelFive(1, attributeId));
    }
}
