package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

public class AssessmentReportMetadataMother {

    public static AssessmentReportMetadata createWithFullMetadata() {
        return new AssessmentReportMetadata(
            "intro",
            "prosAndCons",
            "steps",
            "participants"
        );
    }

    public static AssessmentReportMetadata createWithPartialMetadata() {
        return new AssessmentReportMetadata(
            null,
            "   ",
            "",
            "participants"
        );
    }
}
