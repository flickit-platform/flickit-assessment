package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.application.port.in.assessment.AssessmentColorDto;
import org.springframework.stereotype.Component;

@Component
public class AssessmentColorMapper {
    public AssessmentColorDto mapJpaEntityToColorDto(AssessmentColorEntity assessmentColorEntity) {
        return new AssessmentColorDto(
            assessmentColorEntity.getId(),
            assessmentColorEntity.getTitle(),
            assessmentColorEntity.getColorCode()
        );
    }
}
