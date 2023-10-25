package org.flickit.flickitassessmentcore.test.fixture.application;

import org.flickit.flickitassessmentcore.application.domain.AssessmentKit;

public class AssessmentKitMother {

    private static long id = 134L;

    public static AssessmentKit kit() {
        return new AssessmentKit(id++, MaturityLevelMother.allLevels());
    }
}
