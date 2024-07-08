package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.SpaceAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAssessmentRoleService implements GrantUserAssessmentRoleUseCase {

    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final SpaceAccessChecker spaceAccessChecker;
    private final CreateAssessmentSpaceUserAccessPort createSpaceUserAccessPort;

    @Override
    public void grantAssessmentUserRole(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!spaceAccessChecker.hasAccess(param.getAssessmentId(), param.getUserId()))
            createSpaceUserAccessPort.persist(toCreateSpaceAccessPortParam(param));

        grantUserAssessmentRolePort.persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }

    private CreateAssessmentSpaceUserAccessPort.Param toCreateSpaceAccessPortParam(Param param) {
        return new CreateAssessmentSpaceUserAccessPort.Param(param.getAssessmentId(),
            param.getUserId(),
            param.getCurrentUserId(),
            LocalDateTime.now());
    }
}
