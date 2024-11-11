package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.DeleteAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.flickit.assessment.kit.common.ErrorMessageKey.ANSWER_OPTION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AnswerOptionPersistenceJpaAdapter implements
    LoadAnswerOptionsPort,
    CreateAnswerOptionPort,
    UpdateAnswerOptionPort,
    DeleteAnswerOptionPort {

    private final AnswerOptionJpaRepository repository;
    private final QuestionJpaRepository questionRepository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public void updateTitle(UpdateAnswerOptionPort.UpdateTitleParam param) {
        repository.updateTitle(param.answerOptionId(),
            param.kitVersionId(),
            param.title(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void update(UpdateAnswerOptionPort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.answerOptionId(), param.kitVersionId()))
            throw new ResourceNotFoundException(ANSWER_OPTION_ID_NOT_FOUND);

        repository.update(param.answerOptionId(),
            param.kitVersionId(),
            param.index(),
            param.title(),
            param.value(),
            param.lastModificationTime(),
            param.lastModifiedBy());
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
    public List<AnswerOption> loadByRangeIdInAndKitVersionId(Set<Long> rangeIds, long kitVersionId) {
        Sort sortByIndex = Sort.by(AnswerOptionJpaEntity.Fields.index);
        var optionEntities = repository.findAllByAnswerRangeIdInAndKitVersionId(rangeIds, kitVersionId, sortByIndex);

        return optionEntities.stream()
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
