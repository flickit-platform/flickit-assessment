package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

public class AssessmentReportMetadataMother {

    public static AssessmentReportMetadata createWithFullMetaData() {
        return new AssessmentReportMetadata(
            "intro",
            "prosAndCons",
            "steps",
            "participants"
        );
    }

    public static AssessmentReportMetadata createWithPartialMetaData() {
        return new AssessmentReportMetadata(
            null,
            "prosAndCons",
            "steps",
            "participants"
        );
    }
}
