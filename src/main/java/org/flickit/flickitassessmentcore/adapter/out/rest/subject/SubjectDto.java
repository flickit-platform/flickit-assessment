package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.flickitassessmentcore.domain.calculate.Subject;

import java.util.List;

public record SubjectDto(Long id,
                         @JsonProperty("quality_attributes")
                         List<QualityAttributeDto> qualityAttributes) {

    public Subject dtoToDomain() {
        return Subject.builder()
            .id(id)
            .qualityAttributes(qualityAttributes.stream()
                .map(QualityAttributeDto::dtoToDomain)
                .toList())
            .build();
    }
}
