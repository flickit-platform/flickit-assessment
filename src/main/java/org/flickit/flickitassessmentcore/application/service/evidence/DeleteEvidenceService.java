package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.DeleteEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.DeleteEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteEvidenceService implements DeleteEvidenceUseCase {

    private final DeleteEvidencePort deleteEvidence;

    @Override
    public void deleteEvidence(Param param) {
        deleteEvidence.deleteEvidence(new DeleteEvidencePort.Param(param.getId()));
    }
}
