package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.VisibilityType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentReportMother {

    public static AssessmentReport reportWithMetadata(AssessmentReportMetadata metadata) {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            metadata,
            false,
            VisibilityType.RESTRICTED,
            UUID.randomUUID(),
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
            VisibilityType.RESTRICTED,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId,
            userId);
    }

    public static AssessmentReport publicAndPublishedReport() {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            AssessmentReportMetadataMother.fullMetadata(),
            true,
            VisibilityType.PUBLIC,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId,
            userId);
    }

    public static AssessmentReport restrictedAndPublishedReport() {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            AssessmentReportMetadataMother.fullMetadata(),
            true,
            VisibilityType.RESTRICTED,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId,
            userId);
    }

    public static AssessmentReport publicAndNotPublishedReport() {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            AssessmentReportMetadataMother.fullMetadata(),
            false,
            VisibilityType.PUBLIC,
            UUID.randomUUID(),
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
            VisibilityType.RESTRICTED,
            null,
            null,
            null,
            null,
            null);
    }

    public static AssessmentReport withVisibility(VisibilityType visibility) {
        var userId = UUID.randomUUID();
        return new AssessmentReport(UUID.randomUUID(),
            UUID.randomUUID(),
            null,
            true,
            visibility,
            UUID.randomUUID(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId,
            userId);
    }
}
