package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.kit.application.domain.QuestionImpact;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionImpactMapper {
    public static QuestionImpact mapToDomainModel(QuestionImpactJpaEntity entity) {
        return new QuestionImpact(
            entity.getId(),
            entity.getQualityAttributeId(),
            entity.getMaturityLevelId(),
            entity.getWeight(),
            entity.getQuestionId()
        );
    }

    public static QuestionImpactJpaEntity mapToJpaEntity(QuestionImpact impact) {
        return new QuestionImpactJpaEntity(
            null,
            null,
            impact.getWeight(),
            impact.getQuestionId(),
            impact.getAttributeId(),
            impact.getMaturityLevelId()
        );
    }
}
