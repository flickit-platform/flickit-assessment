package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.ADVICE_ITEM_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements
    CreateAdviceItemPort,
    DeleteAdviceItemPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public UUID persist(AdviceItem adviceItem) {
        var entity = AdviceItemMapper.toJpaEntity(adviceItem);
        return repository.save(entity).getId();
    }

    @Override
    public void deleteAdviceItem(UUID id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException(ADVICE_ITEM_ID_NOT_FOUND);

        repository.deleteById(id);
    }
}
