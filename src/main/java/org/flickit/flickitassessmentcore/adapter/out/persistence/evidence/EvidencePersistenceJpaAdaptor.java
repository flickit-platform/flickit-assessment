package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements CreateEvidencePort {

    private final EvidenceJpaRepository repository;

    @Override
    public Result createEvidence(Param param) {
        return new Result(EvidenceMapper.toDomainModel(repository.save(EvidenceMapper.toJpaEntity(param.evidence()))));
    }
}
