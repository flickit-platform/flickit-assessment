package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue.AssessmentSubjectValueMapper;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueMapper;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.stream.Collectors;

public class AssessmentResultMapper {

    public static AssessmentResult mapToDomainModel(AssessmentResultJpaEntity assessmentResultEntity) {
        return new AssessmentResult(
            assessmentResultEntity.getId(),
            AssessmentMapper.mapToDomainModel(assessmentResultEntity.getAssessment()),
            assessmentResultEntity.getIsValid(),
            assessmentResultEntity.getMaturityLevelId(),
            assessmentResultEntity.getQualityAttributeValues().stream().map(QualityAttributeValueMapper::mapToDomainModel).collect(Collectors.toList()),
            assessmentResultEntity.getAssessmentSubjectValues().stream().map(AssessmentSubjectValueMapper::mapToDomainModel).collect(Collectors.toList())
        );
    }

    public static AssessmentResultJpaEntity mapToJpaEntity(AssessmentResult assessmentResult) {
        return new AssessmentResultJpaEntity(
            assessmentResult.getId(),
            AssessmentMapper.mapToJpaEntity(assessmentResult.getAssessment()),
            assessmentResult.getMaturityLevelId(),
            assessmentResult.getIsValid(),
            assessmentResult.getQualityAttributeValues().stream().map(QualityAttributeValueMapper::mapToJpaEntity).collect(Collectors.toList()),
            assessmentResult.getAssessmentSubjectValues().stream().map(AssessmentSubjectValueMapper::mapToJpaEntity).collect(Collectors.toList())
        );
    }
}
