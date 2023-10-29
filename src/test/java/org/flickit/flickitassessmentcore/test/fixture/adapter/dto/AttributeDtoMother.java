package org.flickit.flickitassessmentcore.test.fixture.adapter.dto;

import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;

public class AttributeDtoMother {

    public static QualityAttributeDto createAttributeDto(Long attributeId) {
        return new QualityAttributeDto(
            attributeId,
            1
        );
    }
}
