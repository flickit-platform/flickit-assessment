package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.advice.common.ErrorMessageKey.ADVICE_ITEM_ID_NOT_FOUND;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements
    CreateAdviceItemPort,
    UpdateAdviceItemPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public UUID persist(AdviceItem adviceItem) {
        var entity = AdviceItemMapper.toJpaEntity(adviceItem);
        return repository.save(entity).getId();
    }

    @Override
    public void updateAdviceItem(AdviceItem adviceItem) {
        if (!repository.existsByIdAndAssessmentResultId(adviceItem.getId(), adviceItem.getAssessmentResultId()))
            throw new ResourceNotFoundException(ADVICE_ITEM_ID_NOT_FOUND);

        repository.updateById(adviceItem.getId(),
            adviceItem.getTitle(),
            adviceItem.getDescription(),
            adviceItem.getCost().ordinal(),
            adviceItem.getPriority().ordinal(),
            adviceItem.getImpact().ordinal(),
            adviceItem.getLastModificationTime(),
            adviceItem.getLastModifiedBy());
    }
}
