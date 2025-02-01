package org.flickit.assessment.core.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AdviceItem;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.adviceitem.LoadAdviceItemsPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
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
        return repository.findByAssessmentResultId(assessmentResultId).stream()
            .map(AdviceItemMapper::mapToDomainModel)
            .toList();
    }
}
