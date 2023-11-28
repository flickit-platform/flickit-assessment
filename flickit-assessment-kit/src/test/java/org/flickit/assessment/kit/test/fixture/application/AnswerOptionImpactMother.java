package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;

public class AnswerOptionImpactMother {

    public static AnswerOptionImpact createAnswerOptionImpact(Long optionId, double value) {
        return new AnswerOptionImpact(optionId, value);
    }
}
