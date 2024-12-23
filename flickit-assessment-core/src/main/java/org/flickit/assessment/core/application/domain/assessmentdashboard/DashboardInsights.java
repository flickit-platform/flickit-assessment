package org.flickit.assessment.core.application.domain.assessmentdashboard;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardInsights(List<InsightTime> insights, long attributesCount, long subjectsCount) {

    public record InsightTime(LocalDateTime insightTime) {
    }
}
