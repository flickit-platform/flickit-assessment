package org.flickit.assessment.core.adapter.out.persistence.attributeinsight;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
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

    public static AttributeInsightJpaEntity mapCreateParamToJpaEntity(CreateAttributeInsightPort.Param param) {
        return new AttributeInsightJpaEntity(
            param.assessmentResultId(),
            param.attributeId(),
            param.aiInsight(),
            null,
            param.aiInsightTime(),
            null,
            param.aiInputPath()
        );
    }
}
