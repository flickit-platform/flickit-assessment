package org.flickit.assessment.core.application.domain.insight;


import java.time.LocalDateTime;

public record Insight(InsightDetail defaultInsight,
                      InsightDetail assessorInsight,
                      boolean editable,
                      Boolean approved) {
    public record InsightDetail(String insight,
                                LocalDateTime creationTime,
                                boolean isValid) {
    }
}