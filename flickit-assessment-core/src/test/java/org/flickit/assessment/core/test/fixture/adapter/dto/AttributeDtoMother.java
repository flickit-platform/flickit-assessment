package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.rest.qualityattribute.QualityAttributeDto;

public class AttributeDtoMother {

    public static QualityAttributeDto createAttributeDto(Long attributeId) {
        return new QualityAttributeDto(
            attributeId,
            1
        );
    }
}
