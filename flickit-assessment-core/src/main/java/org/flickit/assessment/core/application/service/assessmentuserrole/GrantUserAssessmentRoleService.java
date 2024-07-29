package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.NotificationType;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.notification.GrantAssessmentUserRolePayLoad;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.notification.SendNotificationPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAssessmentRoleService implements GrantUserAssessmentRoleUseCase {

    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;
    private final CreateAssessmentSpaceUserAccessPort createSpaceUserAccessPort;
    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;
    private final SendNotificationPort sendNotificationPort;

    @Override
    public void grantAssessmentUserRole(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(param.getAssessmentId(), param.getUserId()))
            createSpaceUserAccessPort.persist(toCreateSpaceAccessPortParam(param));

        grantUserAssessmentRolePort.persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        sendNotificationPort.sendNotification(param.getUserId(),
            NotificationType.GRANT_USER_ASSESSMENT_ROLE,
            createNotificationData(param));
    }

    private CreateAssessmentSpaceUserAccessPort.Param toCreateSpaceAccessPortParam(Param param) {
        return new CreateAssessmentSpaceUserAccessPort.Param(param.getAssessmentId(),
            param.getUserId(),
            param.getCurrentUserId(),
            LocalDateTime.now());
    }

    private GrantAssessmentUserRolePayLoad createNotificationData(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var role = AssessmentUserRole.valueOfById(param.getRoleId());
        var assigner = loadUserPort.loadById(param.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_USER_NOT_FOUND));

        return new GrantAssessmentUserRolePayLoad(
            new GrantAssessmentUserRolePayLoad.AssessmentModel(assessment.getTitle()),
            new GrantAssessmentUserRolePayLoad.AssignerModel(assigner.getDisplayName()),
            new GrantAssessmentUserRolePayLoad.RoleModel(role == null ? null : role.getTitle())
        );
    }
}
