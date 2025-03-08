package org.flickit.assessment.core.adapter.in.rest.insight.attribute;

import java.time.LocalDateTime;

public record GetAttributeInsightResponseDto(InsightDetail aiInsight,
                                             InsightDetail assessorInsight,
                                             boolean editable,
                                             Boolean approved) {
    public record InsightDetail(String insight,
                                LocalDateTime creationTime,
                                boolean isValid) {
    }
}
