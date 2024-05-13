package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.core.common.AssessmentUserRole;

import java.util.UUID;

public interface LoadUserRoleForAssessmentPort {

    AssessmentUserRole load(UUID assessmentId, UUID userId);
}
