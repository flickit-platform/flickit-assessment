package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssessmentPermissionCheckerService implements AssessmentPermissionChecker {

    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public boolean isAuthorized(UUID assessmentId, UUID userId, AssessmentPermission permission) {
        var currentUserRole = loadUserRoleForAssessmentPort.load(assessmentId, userId);

        if (currentUserRole != null)
            return currentUserRole.hasAccess(permission);

        return false;
    }
}
