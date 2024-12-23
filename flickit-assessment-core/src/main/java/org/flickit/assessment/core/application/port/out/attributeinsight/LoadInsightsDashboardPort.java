package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardInsights;

public interface LoadInsightsDashboardPort {

    DashboardInsights loadInsights(long kitVersionId);
}
