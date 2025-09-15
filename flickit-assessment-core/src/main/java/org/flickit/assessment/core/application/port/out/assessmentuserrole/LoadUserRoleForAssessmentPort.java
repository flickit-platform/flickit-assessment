package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadUserRoleForAssessmentPort {

    Optional<AssessmentUserRole> load(UUID assessmentId, UUID userId);

    Optional<AssessmentUserRoleItem> loadRoleItem(UUID assessmentId, UUID userId);

    List<AssessmentUserRoleItem> loadRoleItems(UUID assessmentId, List<UUID> userId);
}
