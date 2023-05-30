package org.flickit.flickitassessmentcore.adapter.out.persistence.mapper;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;

import java.util.stream.Collectors;

public class AssessmentColorMapper {

    private final AssessmentMapper assessmentMapper = new AssessmentMapper();

    public AssessmentColor mapToDomainModel(AssessmentColorEntity assessmentColorEntity) {
        return new AssessmentColor(
            assessmentColorEntity.getId(),
            assessmentColorEntity.getTitle(),
            assessmentColorEntity.getColorCode(),
            assessmentColorEntity.getAssessments().stream()
                .map(assessmentMapper::mapToDomainModel)
                .collect(Collectors.toList())
        );
    }

    public AssessmentColorEntity mapToJpaEntity(AssessmentColor assessmentColor) {
        return new AssessmentColorEntity(
            assessmentColor.getId(),
            assessmentColor.getTitle(),
            assessmentColor.getColorCode()
        );
    }
}
