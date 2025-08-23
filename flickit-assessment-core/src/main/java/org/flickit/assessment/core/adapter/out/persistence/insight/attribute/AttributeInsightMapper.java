package org.flickit.assessment.core.adapter.out.persistence.insight.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.data.jpa.core.insight.attribute.AttributeInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeInsightMapper {

    static AttributeInsight mapToDomain(AttributeInsightJpaEntity entity) {
        return new AttributeInsight(entity.getAssessmentResultId(),
            entity.getAttributeId(),
            entity.getAiInsight(),
            entity.getAssessorInsight(),
            entity.getAiInsightTime(),
            entity.getAssessorInsightTime(),
            entity.getAiInputPath(),
            entity.getApproved(),
            entity.getLastModificationTime());
    }

    public static AttributeInsightJpaEntity mapToJpaEntity(AttributeInsight attributeInsight) {
        return new AttributeInsightJpaEntity(
            attributeInsight.getAssessmentResultId(),
            attributeInsight.getAttributeId(),
            attributeInsight.getAiInsight(),
            attributeInsight.getAssessorInsight(),
            attributeInsight.getAiInsightTime(),
            attributeInsight.getAssessorInsightTime(),
            attributeInsight.getAiInputPath(),
            attributeInsight.isApproved(),
            attributeInsight.getLastModificationTime()
        );
    }

    public static AttributeInsightJpaEntity mapToJpaEntity(UpdateAttributeInsightPort.AiParam insight) {
        return new AttributeInsightJpaEntity(
            insight.assessmentResultId(),
            insight.attributeId(),
            insight.aiInsight(),
            null,
            insight.aiInsightTime(),
            null,
            insight.aiInputPath(),
            insight.isApproved(),
            insight.lastModificationTime()
        );
    }
}
