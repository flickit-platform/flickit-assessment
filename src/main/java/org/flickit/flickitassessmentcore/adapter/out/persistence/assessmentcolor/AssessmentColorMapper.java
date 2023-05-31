package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.application.port.in.assessment.AssessmentColorDto;

public class AssessmentColorMapper {
    static AssessmentColorDto mapJpaEntityToColorDto(AssessmentColorEntity assessmentColorEntity) {
        return new AssessmentColorDto(
            assessmentColorEntity.getId(),
            assessmentColorEntity.getTitle(),
            assessmentColorEntity.getColorCode()
        );
    }
}
