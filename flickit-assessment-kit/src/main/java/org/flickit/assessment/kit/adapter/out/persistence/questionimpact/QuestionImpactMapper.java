package org.flickit.assessment.kit.adapter.out.persistence.questionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionImpactMapper {
    public static QuestionImpact mapToDomainModel(QuestionImpactJpaEntity entity) {
        return new QuestionImpact(
            entity.getId(),
            entity.getAttributeId(),
            entity.getMaturityLevelId(),
            entity.getWeight(),
            entity.getKitVersionId(),
            entity.getQuestionId(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy()
        );
    }

    public static QuestionImpactJpaEntity mapToJpaEntityToPersist(QuestionImpact impact) {
        return new QuestionImpactJpaEntity(
            null,
            impact.getKitVersionId(),
            impact.getWeight(),
            impact.getQuestionId(),
            impact.getAttributeId(),
            impact.getMaturityLevelId(),
            impact.getCreationTime(),
            impact.getLastModificationTime(),
            impact.getCreatedBy(),
            impact.getLastModifiedBy()
        );
    }

    public static QuestionImpactDslModel mapToDsLModel(QuestionImpactJpaEntity entity) {
        return QuestionImpactDslModel.builder()
            .weight(entity.getWeight())
            .build();
    }
}
