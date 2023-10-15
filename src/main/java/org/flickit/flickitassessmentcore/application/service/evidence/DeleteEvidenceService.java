package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CheckEvidenceExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.DeleteEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_NOT_FOUND;

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
