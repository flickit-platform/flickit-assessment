package org.flickit.assessment.core.application.service.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentinvite.UpdateAssessmentInviteUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.UpdateAssessmentInvitePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentInviteService implements UpdateAssessmentInviteUseCase {

    private final LoadAssessmentInvitePort loadAssessmentInvitationPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAssessmentInvitePort updateAssessmentInvitePort;

    @Override
    public void updateInvite(Param param) {
        var invitation = loadAssessmentInvitationPort.load(param.getInviteId());

        if (!assessmentAccessChecker.isAuthorized(invitation.getAssessmentId(), param.getCurrentUserId(), UPDATE_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAssessmentInvitePort.updateRole(param.getInviteId(), param.getRoleId());
    }
}
