package org.flickit.assessment.core.test.fixture.application;


import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;

public class AttributeLevelTargetMother {

    public static AttributeLevelTarget createAttributeLevelTarget() {
        return new AttributeLevelTarget(0L, 1L);
    }
}
