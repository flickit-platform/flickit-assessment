package org.flickit.assessment.core.adapter.in.rest.insight.assessment;

import java.time.LocalDateTime;

public record GetAssessmentInsightResponseDto(InsightDetail aiInsight,
                                              InsightDetail assessorInsight,
                                              boolean editable,
                                              Boolean approved) {
    public record InsightDetail(String insight,
                                LocalDateTime creationTime,
                                boolean isValid) {
    }
}
