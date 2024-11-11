package org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerOptionImpactPersistenceJpaAdapter implements
    CreateAnswerOptionImpactPort,
    UpdateAnswerOptionImpactPort {

    private final AnswerOptionImpactJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public Long persist(CreateAnswerOptionImpactPort.Param param) {
        var entity = AnswerOptionImpactMapper.mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerOptionImpactId());
        return repository.save(entity).getId();
    }

    @Override
    public void persistAll(List<CreateAnswerOptionImpactPort.Param> params) {
        List<AnswerOptionImpactJpaEntity> entities = params.stream()
            .map(param -> {
                var entity = AnswerOptionImpactMapper.mapToJpaEntity(param);
                entity.setId(sequenceGenerators.generateAnswerOptionImpactId());
                return entity;
            })
            .toList();
        repository.saveAll(entities);
    }

    @Override
    public void update(UpdateAnswerOptionImpactPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.value(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
