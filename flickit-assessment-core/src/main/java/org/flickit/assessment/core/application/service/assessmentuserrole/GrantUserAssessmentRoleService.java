package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.core.common.ErrorMessageKey.GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER;

@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAssessmentRoleService implements GrantUserAssessmentRoleUseCase {

    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Override
    public void grantAssessmentUserRole(Param param) {
        var currentUserRole = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId());
        if (currentUserRole == null || !currentUserRole.hasAccess(GRANT_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getUserId()))
            throw new ResourceNotFoundException(GRANT_ASSESSMENT_USER_ROLE_USER_ID_NOT_MEMBER);

        grantUserAssessmentRolePort.grantUserAssessmentRole(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
