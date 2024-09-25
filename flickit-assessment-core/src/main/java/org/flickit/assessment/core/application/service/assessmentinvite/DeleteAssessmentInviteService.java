package org.flickit.assessment.core.application.service.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.port.in.assessmentinvite.DeleteAssessmentInviteUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.DeleteAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInvitePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT_INVITE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAssessmentInviteService implements DeleteAssessmentInviteUseCase {

    private final DeleteAssessmentInvitePort deleteAssessmentInvitePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentInvitePort loadAssessmentInvitePort;

    @Override
    public void deleteInvite(Param param) {
        AssessmentInvite assessmentInvite = loadAssessmentInvitePort.load(param.getId());
        if (!assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), param.getCurrentUserId(), DELETE_ASSESSMENT_INVITE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteAssessmentInvitePort.delete(param.getId());
    }
}
