package org.flickit.assessment.core.adapter.out.persistence.assessmentanalysis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.data.jpa.core.assessmentanalysis.AssessmentAnalysisJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentAnalysisMapper {

    public static AssessmentAnalysisJpaEntity toJpaEntity(AssessmentAnalysis assessmentAnalysis) {
        return new AssessmentAnalysisJpaEntity(null,
            assessmentAnalysis.getAssessmentResultId(),
            assessmentAnalysis.getType(),
            assessmentAnalysis.getAiAnalysis(),
            assessmentAnalysis.getAssessorAnalysis(),
            assessmentAnalysis.getAiAnalysisTime(),
            assessmentAnalysis.getAssessorAnalysisTime(),
            assessmentAnalysis.getInputPath());
    }

    public static AssessmentAnalysis toDomain(AssessmentAnalysisJpaEntity jpaEntity) {
        return new AssessmentAnalysis(
            jpaEntity.getId(),
            jpaEntity.getAssessmentResultId(),
            jpaEntity.getType(),
            jpaEntity.getAiAnalysis(),
            jpaEntity.getAssessorAnalysis(),
            jpaEntity.getAiAnalysisTime(),
            jpaEntity.getAssessorAnalysisTime(),
            jpaEntity.getInputPath()
        );
    }
}
