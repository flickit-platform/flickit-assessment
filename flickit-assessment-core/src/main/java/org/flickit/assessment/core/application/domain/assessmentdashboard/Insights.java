package org.flickit.assessment.core.application.domain.assessmentdashboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Insights(long total, List<Insight> insights) {

    public record Insight(UUID id, LocalDateTime ai_insight_time, LocalDateTime assessor_insight_time) {
    }
}
