package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface GrantUserAssessmentRolePort {

    void grantUserAssessmentRole(UUID assessmentId, UUID userId, Integer roleId);
}
