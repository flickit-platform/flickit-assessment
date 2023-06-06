package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;

import java.util.stream.Collectors;

public class AssessmentColorMapper {

    public static AssessmentColor mapToDomainModel(AssessmentColorJpaEntity assessmentColorEntity) {
        return new AssessmentColor(
            assessmentColorEntity.getId(),
            assessmentColorEntity.getTitle(),
            assessmentColorEntity.getColorCode(),
            assessmentColorEntity.getAssessments().stream()
                .map(AssessmentMapper::mapToDomainModel)
                .collect(Collectors.toSet())
        );
    }

    public static AssessmentColorJpaEntity mapToJpaEntity(AssessmentColor assessmentColor) {
        return new AssessmentColorJpaEntity(
            assessmentColor.getId(),
            assessmentColor.getTitle(),
            assessmentColor.getColorCode()
        );
    }
}
