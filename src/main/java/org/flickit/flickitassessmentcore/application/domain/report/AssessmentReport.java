package org.flickit.flickitassessmentcore.application.domain.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentReport(AssessmentReportItem assessment,
                               List<TopAttributeItem> topStrengths,
                               List<TopAttributeItem> topWeaknesses,
                               List<SubjectReportItem> subjects) {

    public record AssessmentReportItem(UUID id,
                                       String title,
                                       Long maturityLevelId,
                                       boolean isCalculateValid,
                                       int colorId,
                                       LocalDateTime lastModificationTime) {
    }

    public record TopAttributeItem(Long id) {
    }

    public record SubjectReportItem(Long id, Long maturityLevelId) {
    }
}
