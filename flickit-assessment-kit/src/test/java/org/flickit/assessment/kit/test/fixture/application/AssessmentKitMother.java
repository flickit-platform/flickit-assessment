package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AssessmentKit;

public class AssessmentKitMother {

    public static AssessmentKit kitWithFourLevels() {
        return new AssessmentKit(MaturityLevelMother.fourLevels());
    }

    public static AssessmentKit kitWithFiveLevels() {
        return new AssessmentKit(MaturityLevelMother.fiveLevels());
    }
    public static AssessmentKit kitWithSixLevels() {
        return new AssessmentKit(MaturityLevelMother.sixLevels());
    }


}
