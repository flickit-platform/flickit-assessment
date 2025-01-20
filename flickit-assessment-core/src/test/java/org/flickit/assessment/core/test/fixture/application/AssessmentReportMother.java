package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.util.UUID;

public class AssessmentReportMother {

    public static AssessmentReport reportWithMetadata(AssessmentReportMetadata metadata) {
        return new AssessmentReport(UUID.randomUUID(), UUID.randomUUID(), metadata);
    }
}
