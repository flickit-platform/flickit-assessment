package org.flickit.assessment.core.application.domain.assessmentdashboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Insights(List<Insight> insights, long attributesCount, long subjectsCount) {

    public record Insight(UUID id, LocalDateTime insight_time) {
    }
}
