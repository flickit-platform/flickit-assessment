package org.flickit.assessment.core.application.port.out.attributeinsight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoadInsightsDashboardPort {

    Result loadInsights(long kitVersionId);

    record Result(long total, List<Insight> insights) {

        public record Insight(UUID id, LocalDateTime ai_insight_time, LocalDateTime assessor_insight_time) {
        }
    }
}
