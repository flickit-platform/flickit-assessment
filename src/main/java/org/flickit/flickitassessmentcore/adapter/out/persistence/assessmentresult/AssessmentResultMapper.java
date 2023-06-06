package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;

public class AssessmentResultMapper {

    public static AssessmentResult mapToDomainModel(AssessmentResultJpaEntity assessmentResultEntity) {
        return new AssessmentResult(
            assessmentResultEntity.getId(),
            AssessmentMapper.mapToDomainModel(assessmentResultEntity.getAssessment()));
    }

    public static AssessmentResultJpaEntity mapToJpaEntity(AssessmentResult assessmentResult) {
        return new AssessmentResultJpaEntity(
            assessmentResult.getId(),
            AssessmentMapper.mapToJpaEntity(assessmentResult.getAssessment())
        );
    }
}
