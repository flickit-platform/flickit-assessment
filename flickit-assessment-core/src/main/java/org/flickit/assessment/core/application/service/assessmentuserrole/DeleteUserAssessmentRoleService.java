package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.DeleteUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.DeleteUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserAssessmentRoleService implements DeleteUserAssessmentRoleUseCase {

    private final AssessmentPermissionChecker permissionChecker;
    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final DeleteUserAssessmentRolePort deleteUserAssessmentRolePort;
    private final LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Override
    public void deleteAssessmentUserRole(Param param) {
        if (!permissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId());
        if (Objects.equals(param.getUserId(), spaceOwnerId))
            throw new ValidationException(org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER);

        validateUserRoleDeletionRights(param);

        deleteUserAssessmentRolePort.delete(param.getAssessmentId(), param.getUserId());
    }

    private void validateUserRoleDeletionRights(Param param) {
        if (!loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId())) {
            loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())
                .filter(userRoleItem ->
                    userRoleItem.getRole().equals(AssessmentUserRole.REPORT_VIEWER)
                        && param.getCurrentUserId().equals(userRoleItem.getCreatedBy()))
                .orElseThrow(() -> new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED));
        }
    }
}
