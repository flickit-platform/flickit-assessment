package org.flickit.assessment.advice.adapter.out.persistence.attributeleveltarget;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.attributeleveltarget.CreateAttributeLevelTargetPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaRepository;
import org.flickit.assessment.data.jpa.advice.attributeleveltarget.AttributeLevelTargetJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ADVICE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AttributeLevelTargetPersistenceJpaAdapter implements CreateAttributeLevelTargetPort {

    private final AttributeLevelTargetJpaRepository repository;
    private final AdviceJpaRepository adviceRepository;

    @Override
    public void persistAll(UUID adviceId, List<AttributeLevelTarget> attributeLevelTargets) {
        var adviceEntity = adviceRepository.findById(adviceId)
            .orElseThrow(()->new ResourceNotFoundException(CREATE_ADVICE_ADVICE_NOT_FOUND));
        var attributeLevelTargetEntities = attributeLevelTargets.stream().map(a -> AttributeLevelTargetMapper.mapToEntity(a, adviceEntity))
            .toList();
        repository.saveAll(attributeLevelTargetEntities);
    }
}
