package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.AssessmentKit;

public class AssessmentKitMother {

    public static AssessmentKit kit() {
        return AssessmentKit.builder()
            .maturityLevels(MaturityLevelMother.allLevels())
            .build();
    }
}
