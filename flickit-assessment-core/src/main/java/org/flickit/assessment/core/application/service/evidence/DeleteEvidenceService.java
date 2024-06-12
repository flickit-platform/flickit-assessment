package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.DeleteEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteEvidenceService implements DeleteEvidenceUseCase {

    private final DeleteEvidencePort deleteEvidencePort;
    private final LoadEvidencePort loadEvidencePort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void deleteEvidence(Param param) {
        var evidence = loadEvidencePort.loadNotDeletedEvidence(param.getId());

        if (!Objects.equals(evidence.getCreatedById(), param.getCurrentUserId()) ||
            !assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), DELETE_EVIDENCE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteEvidencePort.deleteById(param.getId());
    }
}
