package org.flickit.assessment.core.application.service.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentinvite.UpdateAssessmentInviteUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.UpdateAssessmentInvitePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_FOUND;

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

        if (!assessmentAccessChecker.isAuthorized(invitation.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!AssessmentUserRole.isValidId(param.getRoleId()))
            throw new ValidationException(UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_FOUND);

        updateAssessmentInvitePort.updateRole(param.getInviteId(), param.getRoleId());
    }
}
