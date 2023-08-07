package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.Assessment;

import java.util.UUID;

public class AssessmentMother {

    public static Assessment assessment(){
        return Assessment.builder()
            .id(UUID.randomUUID())
            .assessmentKit(AssessmentKitMother.kit())
            .build();
    }
}
