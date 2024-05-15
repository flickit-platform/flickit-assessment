package org.flickit.assessment.common.permission;

import java.util.UUID;

public interface AssessmentPermissionChecker {

    boolean isAuthorized(UUID assessmentId, UUID userId, AssessmentPermission permission);
}
