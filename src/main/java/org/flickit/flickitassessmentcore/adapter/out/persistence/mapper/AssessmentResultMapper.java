package org.flickit.flickitassessmentcore.adapter.out.persistence.mapper;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentResultEntity;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.stream.Collectors;

public class AssessmentResultMapper {

    private final AssessmentMapper assessmentMapper = new AssessmentMapper();
    private final QualityAttributeValueMapper qualityAttributeValueMapper = new QualityAttributeValueMapper();
    private final AnswerMapper answerMapper = new AnswerMapper();

    public AssessmentResult mapToDomainModel(AssessmentResultEntity assessmentResultEntity) {
        return new AssessmentResult(
            assessmentResultEntity.getId(),
            assessmentMapper.mapToDomainModel(assessmentResultEntity.getAssessment()),
            assessmentResultEntity.getAnswers().stream()
                .map(answerMapper::mapToDomainModel)
                .collect(Collectors.toList()),
            assessmentResultEntity.getQualityAttributeValues().stream()
                .map(qualityAttributeValueMapper::mapToDomainModel)
                .collect(Collectors.toList())
        );
    }

    public AssessmentResultEntity mapToJpaEntity(AssessmentResult assessmentResult) {
        return new AssessmentResultEntity(
            assessmentResult.getId(),
            assessmentMapper.mapToJpaEntity(assessmentResult.getAssessment())
        );
    }
}
