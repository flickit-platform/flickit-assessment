package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.CheckEvidenceExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.DeleteEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteEvidenceService implements DeleteEvidenceUseCase {

    private final DeleteEvidencePort deleteEvidencePort;
    private final CheckEvidenceExistencePort checkEvidenceExistencePort;

    @Override
    public void deleteEvidence(Param param) {
        if (!checkEvidenceExistencePort.existsById(param.getId()))
            throw new ResourceNotFoundException(DELETE_EVIDENCE_EVIDENCE_NOT_FOUND);
        deleteEvidencePort.deleteById(param.getId());
    }
}
