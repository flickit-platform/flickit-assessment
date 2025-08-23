package org.flickit.assessment.core.adapter.out.persistence.insight.assessment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.data.jpa.core.insight.assessment.AssessmentInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentInsightMapper {

    public static AssessmentInsightJpaEntity toJpaEntity(AssessmentInsight assessmentInsight) {
        return new AssessmentInsightJpaEntity(
            null,
            assessmentInsight.getAssessmentResultId(),
            assessmentInsight.getInsight(),
            assessmentInsight.getInsightTime(),
            assessmentInsight.getLastModificationTime(),
            assessmentInsight.getInsightBy(),
            assessmentInsight.isApproved()
        );
    }

    public static AssessmentInsight mapToDomain(AssessmentInsightJpaEntity entity) {
        return new AssessmentInsight(
            entity.getId(),
            entity.getAssessmentResultId(),
            entity.getInsight(),
            entity.getInsightTime(),
            entity.getLastModificationTime(),
            entity.getInsightBy(),
            entity.getApproved()
        );
    }
}
