package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;

import java.util.List;
import java.util.UUID;

public interface GrantUserAssessmentRolePort {

    void persist(UUID assessmentId, UUID userId, Integer roleId);

    void persistAll(List<AssessmentUserRoleItem> assementUserRoleItemList);
}
