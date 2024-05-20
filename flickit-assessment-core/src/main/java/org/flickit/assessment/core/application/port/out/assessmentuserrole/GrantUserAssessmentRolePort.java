package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface GrantUserAssessmentRolePort {

    void persist(UUID assessmentId, UUID userId, Integer roleId);
}
