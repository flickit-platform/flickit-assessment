package org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute;

import org.flickit.flickitassessmentcore.domain.calculate.QualityAttribute;

public record QualityAttributeDto(Long id, Integer weight) {

    public QualityAttribute dtoToDomain() {
        return QualityAttribute.builder()
            .id(id)
            .weight(weight)
            .build();
    }
}
