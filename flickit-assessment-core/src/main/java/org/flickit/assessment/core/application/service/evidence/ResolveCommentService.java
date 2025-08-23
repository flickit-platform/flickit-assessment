package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.ResolveCommentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.ResolveCommentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_COMMENT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_OWN_COMMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ResolveCommentService implements ResolveCommentUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final ResolveCommentPort resolveCommentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void resolveComment(Param param) {
        Evidence comment = loadEvidencePort.loadNotDeletedEvidence(param.getId());

        checkUserAccess(comment.getAssessmentId(), comment.getCreatedById(), param.getCurrentUserId());

        int positive = EvidenceType.POSITIVE.ordinal();
        int negative = EvidenceType.NEGATIVE.ordinal();
        Integer type = comment.getType();
        if (type != null && (type == positive || type == negative))
            throw new ValidationException(RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE);

        resolveCommentPort.resolveComment(comment.getId(), param.getCurrentUserId(), LocalDateTime.now());
    }

    private void checkUserAccess(UUID assessmentId, UUID commenterId, UUID currentUserId) {
        boolean hasAccess = commenterId.equals(currentUserId)
            ? assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, RESOLVE_OWN_COMMENT)
            : assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, RESOLVE_COMMENT);

        if (!hasAccess)
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

}
