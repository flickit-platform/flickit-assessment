package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.assessmentdashboard.Insights;

public interface LoadInsightsDashboardPort {

    Insights loadInsights(long kitVersionId);
}
