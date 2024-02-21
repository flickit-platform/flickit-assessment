package org.flickit.assessment.advice.adapter.out.persistence.attributeleveltarget;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advice.CalculateAdviceUseCase.AttributeLevelTarget;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;
import org.flickit.assessment.data.jpa.advice.attributeleveltarget.AttributeLevelTargetJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeLevelTargetMapper {

    public static AttributeLevelTargetJpaEntity mapToEntity(AttributeLevelTarget attributeLevelTarget, AdviceJpaEntity adviceEntity) {
        return new AttributeLevelTargetJpaEntity(
            null,
            adviceEntity,
            attributeLevelTarget.attributeId(),
            attributeLevelTarget.maturityLevelId()
        );
    }
}
