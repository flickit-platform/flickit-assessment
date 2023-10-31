package org.flickit.assessment.core.adapter.out.rest.qualityattribute;

import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QualityAttributeDtoTest {

    @Test
    void testDtoToDomain() {
        QualityAttributeDto dto = new QualityAttributeDto(123L, 3);

        QualityAttribute domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.weight(), domain.getWeight());
    }
}
