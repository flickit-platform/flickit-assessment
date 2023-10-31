package org.flickit.assessment.core.adapter.out.rest.answeroption;

import org.flickit.assessment.core.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto;
import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnswerOptionDtoTest {

    @Test
    void testDtoToDomain() {
        List<AnswerOptionImpactDto> impactsDtoList = List.of(
            new AnswerOptionImpactDto(123L,0.2, new QuestionImpactDto(200L, 2, 210L, 220L)),
            new AnswerOptionImpactDto(124L, 0.3, new QuestionImpactDto(300L, 3, 310L, 320L))
        );
        AnswerOptionDto dto = new AnswerOptionDto(123L, 223L, impactsDtoList);

        AnswerOption domain = dto.dtoToDomain();

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.questionId(), domain.getQuestionId());
        assertNotNull(domain.getImpacts());
        assertEquals(impactsDtoList.size(), domain.getImpacts().size());

        for(int i=0; i<impactsDtoList.size(); i++) {
            assertEquals(impactsDtoList.get(i).value(), domain.getImpacts().get(i).getValue());
            assertNotNull(domain.getImpacts().get(i).getQuestionImpact());
            assertEquals(impactsDtoList.get(i).questionImpact().weight(), domain.getImpacts().get(i).getQuestionImpact().getWeight());
            assertEquals(impactsDtoList.get(i).questionImpact().maturityLevelId(), domain.getImpacts().get(i).getQuestionImpact().getMaturityLevelId());
        }
    }
}
