package org.flickit.assessment.core.adapter.out.rest.levelcompetence;

import org.flickit.assessment.core.application.domain.LevelCompetence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LevelCompetenceDtoTest {

    @Test
    void testDtoToDomain() {
        LevelCompetenceDto dto = new LevelCompetenceDto(123L, 75, 234L);

        LevelCompetence domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.value(), domain.getValue());
        assertEquals(dto.maturityLevelId(), domain.getMaturityLevelId());
    }
}
