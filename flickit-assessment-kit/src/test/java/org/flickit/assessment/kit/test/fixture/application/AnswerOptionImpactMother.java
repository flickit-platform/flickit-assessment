package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;

public class AnswerOptionImpactMother {

    private static Long id = 134L;

    public static AnswerOptionImpact createAnswerOptionImpact(Long optionId, double value) {
        return new AnswerOptionImpact(id++, optionId, value);
    }
}
