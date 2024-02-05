package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto;
import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;

public class AnswerOptionImpactDtoMother {

    private static long answerOptionImpactId = 134L;

    public static AnswerOptionImpactDto answerOptionImpactDto(QuestionImpactDto questionImpactDto) {
        return new AnswerOptionImpactDto(
            answerOptionImpactId++,
            1.0,
            questionImpactDto
        );
    }
}
