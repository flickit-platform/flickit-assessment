package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

public class AssessmentReportMetadataMother {

    public static AssessmentReportMetadata createSimpleMetadata() {
        return new AssessmentReportMetadata("intro",
            "prosAndCons",
            "steps",
            "participant");
    }

    public static AssessmentReportMetadata createEmptyMetadata() {
        return new AssessmentReportMetadata(null, null, null, null);
    }
}
