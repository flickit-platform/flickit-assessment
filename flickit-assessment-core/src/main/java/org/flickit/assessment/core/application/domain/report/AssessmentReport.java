package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentReport(AssessmentReportItem assessment,
                               List<AttributeReportItem> attributes,
                               List<MaturityLevel> maturityLevels,
                               List<SubjectReportItem> subjects) {

    public record AssessmentReportItem(UUID id,
                                       String title,
                                       AssessmentKitItem assessmentKit,
                                       AssessmentMaturityLevel maturityLevel,
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
            ExpertGroup expertGroup
        ) {
            public record ExpertGroup(Long id, String title) {}
        }

        public record AssessmentMaturityLevel(
            Long id,
            String title,
            Integer value,
            Integer index
        ) {}
    }

    public record AttributeReportItem(
        Long id,
        String title,
        int maturityLevelIndex
    ) {}

    public record SubjectReportItem(
        Long id,
        String title,
        Integer index,
        String description,
        SubjectMaturityLevel maturityLevel
    ) {
        public record SubjectMaturityLevel(Long id, String title) {}
    }
}
