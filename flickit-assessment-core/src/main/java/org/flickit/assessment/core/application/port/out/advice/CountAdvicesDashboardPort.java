package org.flickit.assessment.core.application.port.out.advice;

import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAdvices;

import java.util.UUID;

public interface CountAdvicesDashboardPort {

    DashboardAdvices loadAdviceDashboard(UUID assessmentResultId);
}
