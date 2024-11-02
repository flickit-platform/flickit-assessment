package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.ANSWER_RANGE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AnswerRangePersistenceJpaAdapter implements CreateAnswerRangePort, UpdateAnswerRangePort {

    private final AnswerRangeJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public long persist(CreateAnswerRangePort.Param param) {
        var entity = AnswerRangeMapper.toJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerRangeId());
        return repository.save(entity).getId();
    }

    @Override
    public void update(UpdateAnswerRangePort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.answerRangeId(), param.kitVersionId()))
            throw new ResourceNotFoundException(ANSWER_RANGE_ID_NOT_FOUND);

        repository.update(param.answerRangeId(),
            param.kitVersionId(),
            param.title(),
            param.reusable(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
