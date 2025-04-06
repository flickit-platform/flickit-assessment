package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.Measure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentReportItem(UUID id,
                                   UUID assessmentResultId,
                                   String title,
                                   String shortTitle,
                                   String insight,
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
        KitLanguage language,
        Integer maturityLevelCount,
        Integer questionsCount,
        List<MaturityLevel> maturityLevels,
        List<QuestionnaireReportItem> questionnaires,
        List<Measure> measures,
        ExpertGroup expertGroup) {

        public record ExpertGroup(Long id, String title, String picture) {
        }
    }

    public record Space(
        Long id,
        String title) {
    }
}
