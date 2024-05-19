package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface DeleteUserAssessmentRolePort {

    void deleteUserAssessmentRole(UUID assessmentId, UUID userId, Integer roleId);
}
