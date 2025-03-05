package org.flickit.assessment.core.adapter.in.rest.insight.subject;

import java.time.LocalDateTime;

public record GetSubjectInsightResponseDto(InsightDetail aiInsight,
                                           InsightDetail assessorInsight,
                                           boolean editable,
                                           Boolean approved) {
    public record InsightDetail(String insight,
                                LocalDateTime creationTime,
                                boolean isValid) {
    }
}
