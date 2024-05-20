package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_NOT_FOUND;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssessmentPermissionCheckerService implements AssessmentPermissionChecker {

    private static final AssessmentUserRole ASSESSMENT_CREATED_BY_ROLE = MANAGER;
    private static final AssessmentUserRole SPACE_OWNER_ROLE = MANAGER;

    private final GetAssessmentPort getAssessmentPort;
    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public boolean isAuthorized(UUID assessmentId, UUID userId, AssessmentPermission permission) {
        var assessment = getAssessmentPort.getAssessmentById(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_NOT_FOUND));
        if (Objects.equals(userId, assessment.getCreatedBy()))
            return ASSESSMENT_CREATED_BY_ROLE.hasAccess(permission);
        var spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(assessment.getSpaceId());
        if (Objects.equals(userId, spaceOwnerId))
            return SPACE_OWNER_ROLE.hasAccess(permission);
        var currentUserRole = loadUserRoleForAssessmentPort.load(assessmentId, userId);
        if (currentUserRole != null)
            return currentUserRole.hasAccess(permission);

        return false;
    }
}
