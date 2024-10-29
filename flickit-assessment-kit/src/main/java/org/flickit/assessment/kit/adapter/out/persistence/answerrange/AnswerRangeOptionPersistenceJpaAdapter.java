package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerRangeOptionPersistenceJpaAdapter implements LoadAnswerRangePort {

    private final AnswerRangeJpaRepository repository;

    @Override
    public PaginatedResponse<AnswerRange> loadByKitVersionId(long kitVersionId, int page, int size) {
        Page<AnswerRangeJpaEntity> pageResult =  repository.findReusableByKitVersionId(kitVersionId, PageRequest.of(page, size));
        var items = pageResult
            .getContent()
            .stream()
            .map(AnswerRangeMapper::toDomainModel)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AnswerRangeJpaEntity.Fields.creationTime,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
