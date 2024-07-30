package org.flickit.assessment.core.application.service.assessmentinvitee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.EditAssessmentInviteeRoleUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.UpdateAssessmentInviteeRolePort;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_ASSESSMENT_INVITEE_ROLE_INVITE_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class EditAssessmentInviteeRoleService implements EditAssessmentInviteeRoleUseCase {

    private final LoadAssessmentInvitationPort loadAssessmentInvitationPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAssessmentInviteeRolePort updateAssessmentInviteeRolePort;

    @Override
    public void editRole(Param param) {
        var invitation = loadAssessmentInvitationPort.load(param.getInviteId())
            .orElseThrow(() -> new ResourceNotFoundException(EDIT_ASSESSMENT_INVITEE_ROLE_INVITE_ID_NOT_FOUND));

        if (!assessmentAccessChecker.isAuthorized(invitation.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAssessmentInviteeRolePort.updateRole(param.getInviteId(), param.getRoleId());
    }
}
