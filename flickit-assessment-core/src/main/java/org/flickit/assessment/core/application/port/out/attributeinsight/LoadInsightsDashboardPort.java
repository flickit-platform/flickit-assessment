package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardInsights;

import java.util.List;
import java.util.UUID;

public interface LoadInsightsDashboardPort {

    List<DashboardInsights.InsightTime> loadInsights(UUID assessmentResultId);
}
