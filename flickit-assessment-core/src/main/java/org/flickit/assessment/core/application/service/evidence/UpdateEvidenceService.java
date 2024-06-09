package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.UpdateEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateEvidenceService implements UpdateEvidenceUseCase {

    private final UpdateEvidencePort updateEvidencePort;
    private final LoadEvidencePort loadEvidencePort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result updateEvidence(Param param) {
        Evidence evidence = loadEvidencePort.loadNotDeletedEvidence(param.getId());

        if (!Objects.equals(evidence.getCreatedById(), param.getCurrentUserId()) ||
            !assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), UPDATE_EVIDENCE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var updateParam = new UpdateEvidencePort.Param(
            param.getId(),
            param.getDescription(),
            param.getType() != null ? EvidenceType.valueOf(param.getType()).ordinal() : null,
            LocalDateTime.now(),
            param.getCurrentUserId()
        );
        return new UpdateEvidenceUseCase.Result(updateEvidencePort.update(updateParam).id());
    }
}
