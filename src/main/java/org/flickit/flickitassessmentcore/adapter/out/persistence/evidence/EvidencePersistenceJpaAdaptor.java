package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.SaveEvidencePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements SaveEvidencePort, LoadEvidencePort {

    private final EvidenceJpaRepository repository;


    @Override
    public LoadEvidencePort.Result loadEvidence(LoadEvidencePort.Param param) {
        Optional<EvidenceJpaEntity> evidenceEntity = repository.findById(param.id());
        return new LoadEvidencePort.Result(EvidenceMapper.toDomainModel(evidenceEntity.get()));
    }

    @Override
    public SaveEvidencePort.Result saveEvidence(SaveEvidencePort.Param param) {
        return new SaveEvidencePort.Result(repository.save(EvidenceMapper.toJpaEntity(param.evidence())).getId());
    }
}
