package org.flickit.assessment.core.adapter.out.persistence.kit.questionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.QuestionImpact;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionImpactMapper {

    public static QuestionImpact mapToDomainModel(QuestionImpactJpaEntity entity) {
        return new QuestionImpact(
            entity.getId(),
            entity.getWeight(),
            entity.getAttributeId(),
            entity.getMaturityLevelId());
    }
}
