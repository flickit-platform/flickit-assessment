package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentKit;

public class AssessmentKitMother {

    private static long id = 134L;

    public static AssessmentKit kit() {
        return new AssessmentKit(id++,
            "title" + id,
            id,
            KitLanguage.EN,
            MaturityLevelMother.allLevels());
    }

    public static AssessmentKit AssessmentKitWithoutActiveKitVersion() {
        return new AssessmentKit(id++,
            "title" + id,
            null,
            KitLanguage.EN,
            MaturityLevelMother.allLevels());
    }

    public static AssessmentKit persianKit() {
        return new AssessmentKit(id++,
            "title" + id,
            id,
            KitLanguage.FA,
            MaturityLevelMother.allLevels());
    }
}
