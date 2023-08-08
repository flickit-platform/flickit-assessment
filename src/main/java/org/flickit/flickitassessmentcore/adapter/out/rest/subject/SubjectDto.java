package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.flickitassessmentcore.domain.calculate.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.calculate.Subject;

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
