package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements
    UpdateAnswerOptionPort,
    LoadAnswerOptionsByQuestionPort,
    CreateAnswerOptionPort {

    private final AnswerOptionJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public void update(UpdateAnswerOptionPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId) {
        return repository.findByQuestionIdAndKitVersionId(questionId, kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public Long persist(CreateAnswerOptionPort.Param param) {
        var entity = AnswerOptionMapper.mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerOptionId());
        return repository.save(entity).getId();
    }
}
