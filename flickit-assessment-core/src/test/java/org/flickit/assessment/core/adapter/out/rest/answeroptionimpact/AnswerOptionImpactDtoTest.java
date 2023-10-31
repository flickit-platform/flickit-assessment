package org.flickit.assessment.core.adapter.out.rest.answeroptionimpact;

import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerOptionImpactDtoTest {

    @Test
    void testDtoToDomain() {
        QuestionImpactDto questionImpactDto = new QuestionImpactDto(123L, 2, 223L, 323L);
        AnswerOptionImpactDto dto = new AnswerOptionImpactDto(123L, 0.1, questionImpactDto);

        AnswerOptionImpact domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.value(), domain.getValue());
        assertEquals(dto.id(), domain.getId());
        assertNotNull(domain.getQuestionImpact());
        assertEquals(dto.questionImpact().weight(), domain.getQuestionImpact().getWeight());
        assertEquals(dto.questionImpact().maturityLevelId(), domain.getQuestionImpact().getMaturityLevelId());
    }
}
