package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QuestionImpact;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;

public class QuestionImpactMother {

    private static long id = 134L;

    public static QuestionImpact onLevelTwo(int weight, long attributeId) {
        return new QuestionImpact(id++, weight, attributeId, levelTwo().getId());
    }

    public static QuestionImpact onLevelThree(int weight, long attributeId) {
        return new QuestionImpact(id++, weight, attributeId, levelThree().getId());
    }

    public static QuestionImpact onLevelFour(int weight, long attributeId) {
        return new QuestionImpact(id++, weight, attributeId, levelFour().getId());
    }

    public static QuestionImpact onLevelFive(int weight, long attributeId) {
        return new QuestionImpact(id++, weight, attributeId, levelFive().getId());
    }
}
