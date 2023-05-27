package org.flickit.flickitassessmentcore.adapter.out.persistence.AssessmentColor;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.application.port.in.AssessmentProject.AssessmentColorDto;

public class AssessmentColorMapper {
    public AssessmentColorDto mapJpaEntityToColorDto(AssessmentColorEntity assessmentColorEntity) {
        return new AssessmentColorDto(
            assessmentColorEntity.getId(),
            assessmentColorEntity.getTitle(),
            assessmentColorEntity.getColorCode()
        );
    }
}
