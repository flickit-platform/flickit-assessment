package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserRoleForAssessmentPort {

    Optional<AssessmentUserRole> load(UUID assessmentId, UUID userId);
}
