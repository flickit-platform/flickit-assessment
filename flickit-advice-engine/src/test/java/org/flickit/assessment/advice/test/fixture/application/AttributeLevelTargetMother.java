package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;

public class AttributeLevelTargetMother {

    public static AttributeLevelTarget createAttributeLevelTarget() {
        return new AttributeLevelTarget(0L, 1L);
    }
}
