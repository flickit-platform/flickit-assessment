package org.flickit.assessment.core.adapter.out.rest.question;

import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.assessment.core.application.domain.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionDtoTest {

    @Test
    void testDtoToDomain() {
        List<QuestionImpactDto> questionImpactDtoList = List.of(
            new QuestionImpactDto(100L, 1, 110L, 120L),
            new QuestionImpactDto(200L, 2, 210L, 220L)
        );
        QuestionDto dto = new QuestionDto(123L, questionImpactDtoList);

        Question domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertNotNull(domain.getImpacts());
        assertEquals(questionImpactDtoList.size(), domain.getImpacts().size());

        for (int i = 0; i < questionImpactDtoList.size(); i++) {
            assertEquals(questionImpactDtoList.get(i).weight(), domain.getImpacts().get(i).getWeight());
            assertEquals(questionImpactDtoList.get(i).maturityLevelId(), domain.getImpacts().get(i).getMaturityLevelId());
        }
    }
}
