package org.flickit.assessment.core.adapter.in.rest.assessmentinsight;

import java.time.LocalDateTime;

public record GetAssessmentInsightResponseDto(DefaultInsight defaultInsight, AssessorInsight assessorInsight, boolean editable) {

    public record DefaultInsight(String insight) {
    }

    public record AssessorInsight(String insight, LocalDateTime creationTime, boolean isValid) {
    }
}
