package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements CreateAdviceItemPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public UUID persist(AdviceItem adviceItem) {
        var entity = AdviceItemMapper.toJpaEntity(adviceItem);
        return repository.save(entity).getId();
    }
}
