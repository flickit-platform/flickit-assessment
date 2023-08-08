package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.Assessment;

import java.util.UUID;

public class AssessmentMother {

    public static Assessment assessment(){
        return new Assessment(UUID.randomUUID(), AssessmentKitMother.kit());
    }
}
