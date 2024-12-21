package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.ResolveCommentUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.ResolveCommentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_COMMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_USER_ROLE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ResolveCommentService implements ResolveCommentUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final ResolveCommentPort resolveCommentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public void resolveComment(Param param) {
        Evidence evidence = loadEvidencePort.loadNotDeletedEvidence(param.getId());
        if (!assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)) {
            log.warn("User {} is not authorized to resolve comment", param.getCurrentUserId());
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }

        var role = loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_USER_ROLE_ID_NOT_FOUND));
        if (AssessmentUserRole.ASSOCIATE.equals(role) && !evidence.getCreatedById().equals(param.getCurrentUserId())) {
            log.warn("User {} with ASSOCIATE role cannot resolve others comment", param.getCurrentUserId());
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }

        int positive = EvidenceType.POSITIVE.ordinal();
        int negative = EvidenceType.NEGATIVE.ordinal();
        Integer type = evidence.getType();
        if (type != null && (type == positive || type == negative))
            throw new ValidationException(RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE);

        resolveCommentPort.resolveComment(evidence.getId(), param.getCurrentUserId(), LocalDateTime.now());
    }
}
