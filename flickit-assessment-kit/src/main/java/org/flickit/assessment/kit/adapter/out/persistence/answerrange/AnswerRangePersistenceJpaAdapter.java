package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerRangePersistenceJpaAdapter implements CreateAnswerRangePort {

    private final AnswerRangeJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public long persist(Param param) {
        var entity = AnswerRangeMapper.toJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerRangeId());
        return repository.save(entity).getId();
    }
}
