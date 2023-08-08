package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import org.flickit.flickitassessmentcore.domain.QuestionImpact;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionImpactDtoTest {

    @Test
    void dtoToDomain() {
        QuestionImpactDto dto = new QuestionImpactDto(123L, 2, 234L, 345L);

        QuestionImpact domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.weight(), domain.getWeight());
        assertEquals(dto.maturityLevelId(), domain.getMaturityLevelId());
    }
}
