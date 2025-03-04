package org.flickit.assessment.core.application.service.evidence;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_ALL_COMMENTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ResolveAssessmentCommentsService implements ResolveAssessmentCommentsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void resolveAllComments(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), RESOLVE_ALL_COMMENTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

    }
}
