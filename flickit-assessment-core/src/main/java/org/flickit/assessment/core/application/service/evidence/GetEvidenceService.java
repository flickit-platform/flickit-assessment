package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceService implements GetEvidenceUseCase {

    @Override
    public Result getEvidence(Param param) {
        return null;
    }
}
