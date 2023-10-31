package org.flickit.assessment.core.adapter.out.rest.qualityattribute;

import org.flickit.assessment.core.application.domain.QualityAttribute;

public record QualityAttributeDto(Long id, Integer weight) {

    public QualityAttribute dtoToDomain() {
        return new QualityAttribute(id, weight, null);
    }
}
