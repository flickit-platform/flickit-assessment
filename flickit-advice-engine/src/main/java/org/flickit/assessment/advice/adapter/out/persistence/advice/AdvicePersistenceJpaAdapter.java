package org.flickit.assessment.advice.adapter.out.persistence.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessmentadvice.CreateAdvicePort;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdvicePersistenceJpaAdapter implements CreateAdvicePort {

    private final AdviceJpaRepository repository;

    @Override
    public UUID persist(Param param) {
        AdviceJpaEntity entity = repository.save(AdviceMapper.mapToEntity(param));
        return entity.getId();
    }
}
