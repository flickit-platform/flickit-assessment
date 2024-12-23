package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardEvidences;

import java.util.UUID;

public interface LoadEvidencesDashboardPort {

    DashboardEvidences loadEvidencesDashboard(UUID assessmentId);
}
