package org.flickit.assessment.core.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AdviceItem;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.adviceitem.LoadAdviceItemsPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("coreAdviceItemPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements
    CountAdviceItemsPort,
    LoadAdviceItemsPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public int countAdviceItems(UUID assessmentResultId) {
        return repository.countByAssessmentResultId(assessmentResultId);
    }

    @Override
    public List<AdviceItem> loadAll(UUID assessmentResultId) {
        var sort = Sort.by(
            Sort.Order.desc(AdviceItemJpaEntity.Fields.priority),
            Sort.Order.desc(AdviceItemJpaEntity.Fields.impact),
            Sort.Order.asc(AdviceItemJpaEntity.Fields.cost)
        );

        var pageResult = repository.findByAssessmentResultId(assessmentResultId,
            PageRequest.of(0, Integer.MAX_VALUE, sort));

        return pageResult.stream()
            .map(AdviceItemMapper::mapToDomainModel)
            .toList();
    }
}
