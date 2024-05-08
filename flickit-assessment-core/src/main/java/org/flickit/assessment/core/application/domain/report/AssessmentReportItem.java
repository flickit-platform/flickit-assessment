package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentReportItem(UUID id,
                                   String title,
                                   AssessmentKitItem assessmentKit,
                                   MaturityLevel maturityLevel,
                                   Double confidenceValue,
                                   boolean isCalculateValid,
                                   boolean isConfidenceValid,
                                   AssessmentColor color,
                                   LocalDateTime lastModificationTime) {
    public record AssessmentKitItem(
        Long id,
        String title,
        String summary,
        Integer maturityLevelCount,
        ExpertGroup expertGroup) {
        public record ExpertGroup(Long id, String title) {
        }
    }
}
