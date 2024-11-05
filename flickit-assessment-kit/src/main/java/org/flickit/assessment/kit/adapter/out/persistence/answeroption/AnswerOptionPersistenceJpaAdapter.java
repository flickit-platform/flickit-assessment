package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.*;
import org.flickit.assessment.kit.application.domain.AnswerOptionOrder;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.ANSWER_OPTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements
    UpdateAnswerOptionPort,
    LoadAnswerOptionsByQuestionPort,
    CreateAnswerOptionPort,
    DeleteAnswerOptionPort {

    private final AnswerOptionJpaRepository repository;
    private final QuestionJpaRepository questionRepository;
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
    public void updateOrders(List<AnswerOptionOrder> answerOptionOrders, Long kitVersionId, UUID lastModifiedBy) {
        Map<AnswerOptionJpaEntity.EntityId, AnswerOptionOrder> idToModel = answerOptionOrders.stream()
            .collect(Collectors.toMap(
                ao -> new AnswerOptionJpaEntity.EntityId(ao.getId(), kitVersionId),
                ao -> ao
            ));
        List<AnswerOptionJpaEntity> entities = repository.findAllById(idToModel.keySet());
        if (entities.size() != answerOptionOrders.size())
            throw new ResourceNotFoundException(ANSWER_OPTION_ID_NOT_FOUND);

        entities.forEach(x -> {
            AnswerOptionOrder newLevel = idToModel.get(new AnswerOptionJpaEntity.EntityId(x.getId(), kitVersionId));
            x.setIndex(newLevel.getIndex());
            x.setLastModificationTime(LocalDateTime.now());
            x.setLastModifiedBy(lastModifiedBy);
        });
        repository.saveAll(entities);
    }

    @Override
    public List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId) {
        QuestionJpaEntity question = questionRepository.findByIdAndKitVersionId(questionId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));

        return repository.findAllByAnswerRangeIdAndKitVersionIdOrderByIndex(question.getAnswerRangeId(), kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public long persist(CreateAnswerOptionPort.Param param) {
        var entity = AnswerOptionMapper.mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerOptionId());
        return repository.save(entity).getId();
    }

    @Override
    public void delete(Long answerOptionId, Long kitVersionId) {
        if(!repository.existsByIdAndKitVersionId(answerOptionId, kitVersionId))
            throw new ResourceNotFoundException(ANSWER_OPTION_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(answerOptionId, kitVersionId);
    }
}
