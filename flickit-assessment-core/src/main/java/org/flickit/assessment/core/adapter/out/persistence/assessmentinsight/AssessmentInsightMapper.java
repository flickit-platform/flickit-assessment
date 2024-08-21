package org.flickit.assessment.core.adapter.out.persistence.assessmentinsight;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.data.jpa.core.assessmentinsight.AssessmentInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentInsightMapper {

    public static AssessmentInsightJpaEntity toJpaEntity(CreateAssessmentInsightPort.Param param) {
        return new AssessmentInsightJpaEntity(
            null,
            param.assessmentResultId(),
            param.insight(),
            param.insightTime(),
            param.insightBy()
        );
    }

    public static AssessmentInsight mapToDomain(AssessmentInsightJpaEntity entity) {
        return new AssessmentInsight(
            entity.getId(),
            entity.getAssessmentResultId(),
            entity.getInsight(),
            entity.getInsightTime(),
            entity.getInsightBy()
        );
    }
}
