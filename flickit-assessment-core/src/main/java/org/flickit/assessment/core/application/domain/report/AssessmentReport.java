package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.AssessmentColor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentReport(AssessmentReportItem assessment,
                               List<TopAttribute> topStrengths,
                               List<TopAttribute> topWeaknesses,
                               List<SubjectReportItem> subjects) {

    public record AssessmentReportItem(UUID id,
                                       String title,
                                       Long maturityLevelId,
                                       boolean isCalculateValid,
                                       AssessmentColor color,
                                       LocalDateTime lastModificationTime) {
    }

    public record SubjectReportItem(Long id, Long maturityLevelId) {
    }
}
