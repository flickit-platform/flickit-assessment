package org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;

public record QualityAttributeDto(Long id, Integer weight) {

    public QualityAttribute dtoToDomain() {
        return new QualityAttribute(id, weight, null);
    }
}
