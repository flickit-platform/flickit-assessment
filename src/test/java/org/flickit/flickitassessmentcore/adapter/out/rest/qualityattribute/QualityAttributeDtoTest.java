package org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QualityAttributeDtoTest {

    @Test
    void dtoToDomain() {
        QualityAttributeDto dto = new QualityAttributeDto(123L, 3);

        QualityAttribute domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.weight(), domain.getWeight());
    }
}
