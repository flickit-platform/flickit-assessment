package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase;
import org.flickit.assessment.core.application.port.out.evidence.ResolveCommentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_ALL_COMMENTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ResolveAssessmentCommentsService implements ResolveAssessmentCommentsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ResolveCommentPort resolveCommentPort;

    @Override
    public void resolveAllComments(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), RESOLVE_ALL_COMMENTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        resolveCommentPort.resolveAllComments(param.getAssessmentId(),
            param.getCurrentUserId(),
            LocalDateTime.now());
    }
}
