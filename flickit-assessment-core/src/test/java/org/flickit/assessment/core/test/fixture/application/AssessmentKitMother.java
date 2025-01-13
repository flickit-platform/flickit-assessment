package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentKit;

public class AssessmentKitMother {

    private static long id = 134L;

    public static AssessmentKit kit() {
        return new AssessmentKit(id++,
            "title" + id,
            id,
            MaturityLevelMother.allLevels(),
            true);
    }

    public static AssessmentKit publicKit() {
        return new AssessmentKit(id++,
            "title" + id,
            id,
            MaturityLevelMother.allLevels(),
            false);
    }

    public static AssessmentKit AssessmentKitWithoutActiveKitVersion() {
        return new AssessmentKit(id++,
            "title" + id,
            null,
            MaturityLevelMother.allLevels(),
            true);
    }
}
