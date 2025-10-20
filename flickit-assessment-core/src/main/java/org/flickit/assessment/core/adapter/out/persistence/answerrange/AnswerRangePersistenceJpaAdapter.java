package org.flickit.assessment.core.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("coreAnswerRangePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AnswerRangePersistenceJpaAdapter implements LoadAnswerRangePort {

    private final AnswerRangeJpaRepository repository;

    @Override
    public Set<Long> loadIdsByKitVersionId(Long kitVersionId) {
        return repository.findAllByKitVersionId(kitVersionId).stream()
            .map(AnswerRangeJpaEntity::getId)
            .collect(Collectors.toSet());
    }
}
