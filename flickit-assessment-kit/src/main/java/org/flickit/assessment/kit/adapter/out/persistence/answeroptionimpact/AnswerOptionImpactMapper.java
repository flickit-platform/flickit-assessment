package org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionImpactMapper {

    public static AnswerOptionImpact mapToDomainModel(AnswerOptionImpactJpaEntity entity, double optionValue) {
        return new AnswerOptionImpact(
            entity.getId(),
            entity.getOptionId(),
            entity.getValue() != null ? entity.getValue() : optionValue
        );
    }
}
