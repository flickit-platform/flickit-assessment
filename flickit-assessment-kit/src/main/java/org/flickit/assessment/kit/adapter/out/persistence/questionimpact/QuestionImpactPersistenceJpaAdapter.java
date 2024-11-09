package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper.mapToJpaEntityToPersist;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_IMPACT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionImpactPersistenceJpaAdapter implements
    CreateQuestionImpactPort,
    DeleteQuestionImpactPort,
    UpdateQuestionImpactPort,
    LoadQuestionImpactPort {

    private final QuestionImpactJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public Long persist(QuestionImpact impact) {
        var entity = mapToJpaEntityToPersist(impact);
        entity.setId(sequenceGenerators.generateQuestionImpactId());
        return repository.save(entity).getId();
    }

    @Override
    public void delete(Long questionImpactId, Long kitVersionId) {
        if(!repository.existsByIdAndKitVersionId(questionImpactId, kitVersionId))
            throw new ResourceNotFoundException(QUESTION_IMPACT_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(questionImpactId, kitVersionId);
    }

    @Override
    public void updateWeight(UpdateWeightParam param) {
        repository.updateWeight(param.id(),
            param.kitVersionId(),
            param.weight(),
            param.questionId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void update(UpdateQuestionImpactPort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.id(), param.kitVersionId()))
            throw new ResourceNotFoundException(QUESTION_IMPACT_ID_NOT_FOUND);

        repository.update(param.id(),
            param.kitVersionId(),
            param.weight(),
            param.attributeId(),
            param.maturityLevelId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public List<QuestionImpact> loadAllByKitVersionId(long kitVersionId) {
        List<QuestionImpactJpaEntity> entities = repository.findAllByKitVersionId(kitVersionId);

        return entities.stream()
            .map(QuestionImpactMapper::mapToDomainModel)
            .toList();
    }
}
