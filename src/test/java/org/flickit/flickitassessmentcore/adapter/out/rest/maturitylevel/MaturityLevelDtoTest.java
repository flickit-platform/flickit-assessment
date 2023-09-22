package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence.LevelCompetenceDto;
import org.flickit.flickitassessmentcore.application.domain.LevelCompetence;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaturityLevelDtoTest {

    @Test
    void testDtoToDomain() {
        List<LevelCompetenceDto> lcDtoList = List.of(
            new LevelCompetenceDto(123L, 25, 123L),
            new LevelCompetenceDto(234L, 60, 234L)
        );

        MaturityLevelDto dto = new MaturityLevelDto(123L, 2, lcDtoList);

        MaturityLevel domain = dto.dtoToDomain();
        List<LevelCompetence> lcList = domain.getLevelCompetences();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.level(), domain.getLevel());
        assertNotNull(domain.getLevelCompetences());
        assertEquals(lcDtoList.size(), lcList.size());

        for (int i = 0; i < lcDtoList.size(); i++) {
            assertEquals(lcDtoList.get(i).id(), lcList.get(i).getId());
            assertEquals(lcDtoList.get(i).value(), lcList.get(i).getValue());
            assertEquals(lcDtoList.get(i).maturityLevelId(), lcList.get(i).getMaturityLevelId());
        }
    }
}
