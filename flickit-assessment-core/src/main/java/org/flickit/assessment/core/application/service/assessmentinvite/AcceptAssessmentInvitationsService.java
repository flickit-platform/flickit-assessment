package org.flickit.assessment.core.application.service.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.notification.SendNotification;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.port.in.assessmentinvite.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.DeleteAssessmentUserInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentsUserInvitationsPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.core.application.service.assessmentinvite.notification.AcceptAssessmentInvitationNotificationCmd;
import org.flickit.assessment.core.application.service.assessmentuserrole.notification.GrantAssessmentUserRoleNotificationCmd;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AcceptAssessmentInvitationsService implements AcceptAssessmentInvitationsUseCase {

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final LoadAssessmentsUserInvitationsPort loadAssessmentsUserInvitationsPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final DeleteAssessmentUserInvitationPort deleteAssessmentUserInvitationPort;

    @Override
    @SendNotification
    public Result acceptInvitations(Param param) {
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());
        var invitations = loadAssessmentsUserInvitationsPort.loadInvitations(email);

        List<AssessmentUserRoleItem> validInvitations = invitations.stream()
            .filter(AssessmentInvite::isNotExpired)
            .map(i -> toAssessmentUserRoleItem(i, param.getUserId()))
            .toList();

        if (!validInvitations.isEmpty())
            grantUserAssessmentRolePort.persistAll(validInvitations);
        if (!invitations.isEmpty())
            deleteAssessmentUserInvitationPort.deleteAllByEmail(email);

        return new Result(new AcceptAssessmentInvitationNotificationCmd(validInvitations.get(0).getCreatedBy(),
            validInvitations.get(0).getAssessmentId(),
            validInvitations.get(0).getUserId(),
            validInvitations.get(0).getRole()));
    }

    private AssessmentUserRoleItem toAssessmentUserRoleItem(AssessmentInvite invitation, UUID userId) {
        return new AssessmentUserRoleItem(invitation.getAssessmentId(), userId, invitation.getRole(), invitation.getCreatedBy());
    }

    private Result mapToResult(AssessmentUserRoleItem assessmentUserRoleItem) {
        return new Result(new AcceptAssessmentInvitationNotificationCmd(assessmentUserRoleItem.getUserId(),
            assessmentUserRoleItem.getAssessmentId(),
            assessmentUserRoleItem.getUserId(),
            assessmentUserRoleItem.getRole()));
    }
}
