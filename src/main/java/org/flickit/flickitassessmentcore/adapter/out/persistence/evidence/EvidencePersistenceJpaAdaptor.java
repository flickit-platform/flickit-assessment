package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements CreateEvidencePort {

    private final EvidenceJpaRepository repository;

    @Override
    public UUID persist(Param param) {
        EvidenceJpaEntity evidenceJpaEntity = repository.save(EvidenceMapper.toJpaEntity(param));
        return evidenceJpaEntity.getId();
    }
}
