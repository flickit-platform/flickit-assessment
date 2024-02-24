package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.UpdateEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateEvidenceService implements UpdateEvidenceUseCase {

    private final UpdateEvidencePort updateEvidencePort;

    @Override
    public Result updateEvidence(Param param) {
        var updateParam = new UpdateEvidencePort.Param(
            param.getId(),
            param.getDescription(),
            param.getType() != null ? EvidenceType.valueOf(param.getType()).ordinal() : null,
            LocalDateTime.now(),
            param.getLastModifiedById()
        );
        return new UpdateEvidenceUseCase.Result(updateEvidencePort.update(updateParam).id());
    }
}
