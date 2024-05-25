package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface DeleteUserAssessmentRolePort {

    void delete(UUID assessmentId, UUID userId);
}
