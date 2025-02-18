package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentReportMother {

    public static AssessmentReport reportWithMetadata(AssessmentReportMetadata metadata) {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            metadata,
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId,
            userId);
    }

    public static AssessmentReport publishedReportWithMetadata(AssessmentReportMetadata metadata) {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            metadata,
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId,
            userId);
    }

    public static AssessmentReport empty() {
        return new AssessmentReport(null,
            null,
            new AssessmentReportMetadata(null, null, null, null),
            false,
            null,
            null,
            null,
            null);
    }
}
