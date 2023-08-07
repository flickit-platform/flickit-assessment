package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.Assessment;
import org.flickit.flickitassessmentcore.domain.calculate.AssessmentResult;

import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult.AssessmentResultBuilder builder() {
        return AssessmentResult.builder()
            .id(UUID.randomUUID())
            .assessment(AssessmentMother.assessment());
    }
}
