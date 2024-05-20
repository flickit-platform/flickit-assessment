package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface UpdateUserAssessmentRolePort {

    void updateUserAssessmentRole(UUID assessmentId, UUID userId, Integer roleId);
}
