package org.flickit.assessment.core.adapter.out.persistence.attributeinsight;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.data.jpa.core.attributeinsight.AttributeInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeInsightMapper {

    static AttributeInsight mapToDomain(AttributeInsightJpaEntity entity) {
        return new AttributeInsight(entity.getAssessmentResultId(),
            entity.getAttributeId(),
            entity.getAiInsight(),
            entity.getAssessorInsight(),
            entity.getAiInsightTime(),
            entity.getAssessorInsightTime(),
            entity.getAiInputPath());
    }
}
