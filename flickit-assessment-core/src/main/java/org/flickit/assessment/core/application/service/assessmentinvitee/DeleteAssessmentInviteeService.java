package org.flickit.assessment.core.application.service.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.DeleteAssessmentInviteeUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.DeleteAssessmentInviteePort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInviteePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT_INVITEE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAssessmentInviteeService implements DeleteAssessmentInviteeUseCase {

    private final DeleteAssessmentInviteePort deleteAssessmentInviteePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentInviteePort loadAssessmentInviteePort;

    @Override
    public void deleteInvitees(Param param) {
        AssessmentInvitee assessmentInvitee = loadAssessmentInviteePort.loadById(param.getId());
        if (!assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), param.getCurrentUserId(), DELETE_ASSESSMENT_INVITEE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteAssessmentInviteePort.deleteById(param.getId());
    }
}
