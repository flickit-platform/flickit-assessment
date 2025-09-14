package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
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
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ROLE_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER;

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
        validatePermission(param);

        if (!loadAssessmentPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(param.getAssessmentId());
        if (Objects.equals(param.getUserId(), spaceOwnerId))
            throw new ValidationException(DELETE_ASSESSMENT_USER_ROLE_USER_ID_IS_SPACE_OWNER);

        deleteUserAssessmentRolePort.delete(param.getAssessmentId(), param.getUserId());
    }

    void validatePermission(Param param) {
        var hasAccess = permissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE);
        if (!hasAccess) {
            var userRoleItem = loadUserRoleForAssessmentPort.loadRoleItem(param.getAssessmentId(), param.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ROLE_NOT_FOUND));
            if (!userRoleItem.getCreatedBy().equals(param.getCurrentUserId()) || !userRoleItem.getRole().equals(AssessmentUserRole.REPORT_VIEWER))
                throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
