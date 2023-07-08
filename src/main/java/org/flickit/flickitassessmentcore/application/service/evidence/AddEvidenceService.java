package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.evidence.EvidenceMapper;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.AddEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AddEvidenceService implements AddEvidenceUseCase {

    private final AddEvidencePort addEvidence;

    @Override
    public Result addEvidence(Param param) {
        AddEvidencePort.Result result = addEvidence.addEvidence(EvidenceMapper.toAddEvidencePortParam(param));
        return new AddEvidenceUseCase.Result(result.id());
    }
}
