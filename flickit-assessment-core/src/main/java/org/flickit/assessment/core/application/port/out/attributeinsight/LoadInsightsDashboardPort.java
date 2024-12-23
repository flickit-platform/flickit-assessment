package org.flickit.assessment.core.application.port.out.attributeinsight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoadInsightsDashboardPort {

    List<Result.InsightTime> loadInsights(UUID assessmentResultId);

    record Result(List<InsightTime> insights, long attributesCount, long subjectsCount) {

        public record InsightTime(LocalDateTime insightTime) {
        }
    }
}
