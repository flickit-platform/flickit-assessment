package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;

public class AssessmentResultMapper {

    public static AssessmentResult mapToDomainModel(AssessmentResultJpaEntity assessmentResultEntity) {
        return new AssessmentResult(
            assessmentResultEntity.getId(),
            AssessmentMapper.mapToDomainModel(assessmentResultEntity.getAssessment()),
            null,
            assessmentResultEntity.getIsValid()
        );
    }

    public static AssessmentResultJpaEntity mapToJpaEntity(CreateAssessmentResultPort.Param param) {
        return new AssessmentResultJpaEntity(
            null,
            null,
            null,
            param.isValid()
        );
    }
}
