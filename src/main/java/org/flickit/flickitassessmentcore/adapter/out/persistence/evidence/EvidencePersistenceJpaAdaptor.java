package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements
    CreateEvidencePort,
    LoadEvidencesByQuestionPort{

    private final EvidenceJpaRepository repository;

    @Override
    public UUID persist(CreateEvidencePort.Param param) {
        var unsavedEntity = EvidenceMapper.mapCreateParamToJpaEntity(param);
        EvidenceJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public LoadEvidencesByQuestionPort.Result loadEvidencesByQuestionId(LoadEvidencesByQuestionPort.Param param, int page, int size) {
        return new LoadEvidencesByQuestionPort.Result(repository.findByQuestionIdOrderByLastModificationTimeDesc(param.questionId(), PageRequest.of(page, size)).stream()
            .map(EvidenceMapper::toDomainModel)
            .collect(Collectors.toList()));
    }
}
