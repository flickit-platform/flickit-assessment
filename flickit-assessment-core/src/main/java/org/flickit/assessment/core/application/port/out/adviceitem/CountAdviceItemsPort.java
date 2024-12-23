package org.flickit.assessment.core.application.port.out.adviceitem;

import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAdvices;

import java.util.UUID;

public interface CountAdviceItemsPort {

    DashboardAdvices countAdviceItems(UUID assessmentResultId);
}
