package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.assessmentdashboard.Insights;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoadInsightsDashboardPort {

    Insights loadInsights(long kitVersionId);
}
