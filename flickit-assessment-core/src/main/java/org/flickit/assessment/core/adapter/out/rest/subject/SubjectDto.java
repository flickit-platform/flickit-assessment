package org.flickit.assessment.core.adapter.out.rest.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;

import java.util.List;

public record SubjectDto(Long id,
                         @JsonProperty("quality_attributes")
                         List<QualityAttributeDto> qualityAttributes) {

    public Subject dtoToDomain() {
        List<QualityAttribute> qualityAttributesList = qualityAttributes.stream()
            .map(QualityAttributeDto::dtoToDomain)
            .toList();

        return new Subject(id, qualityAttributesList);
    }
}
