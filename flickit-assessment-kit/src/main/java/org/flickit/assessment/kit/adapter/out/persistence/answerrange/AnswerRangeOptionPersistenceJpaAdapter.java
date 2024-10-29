package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerRangeOptionPersistenceJpaAdapter implements LoadAnswerRangePort {

    private final AnswerRangeJpaRepository repository;

    @Override
    public List<AnswerRange> loadByKitVersionId(long kitVersionId) {
        return repository.findReusableByKitVersionId(kitVersionId)
            .stream()
            .map(AnswerRangeMapper::toDomainModel)
            .toList();
    }
}
