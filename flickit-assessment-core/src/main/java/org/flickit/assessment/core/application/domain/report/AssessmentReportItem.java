package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentReportItem(UUID id,
                                   String title,
                                   AssessmentKitItem assessmentKit,
                                   MaturityLevel maturityLevel,
                                   Double confidenceValue,
                                   boolean isCalculateValid,
                                   boolean isConfidenceValid,
                                   LocalDateTime creationTime,
                                   LocalDateTime lastModificationTime,
                                   Space space) {

    public record AssessmentKitItem(
        Long id,
        String title,
        String summary,
        String about,
        Integer maturityLevelCount,
        List<MaturityLevel> maturityLevels,
        ExpertGroup expertGroup) {

        public record ExpertGroup(Long id, String title, String picture) {
        }
    }

    public record Space(
        Long id,
        String title) {
    }
}
