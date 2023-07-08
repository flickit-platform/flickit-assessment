package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.AddEvidencePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements AddEvidencePort {

    private final EvidenceJpaRepository repository;

    @Override
    public Result addEvidence(Param param) {
        EvidenceJpaEntity evidenceJpaEntity = repository.save(
            EvidenceMapper.toJpaEntity(EvidenceMapper.toEvidence(param))
        );
        return new Result(evidenceJpaEntity.getId());
    }
}
