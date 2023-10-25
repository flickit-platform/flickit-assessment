package org.flickit.flickitassessmentcore.test.fixture.adapter.dto;

import org.flickit.flickitassessmentcore.adapter.out.rest.questionimpact.QuestionImpactDto;

public class AnswerOptionImpactDtoMother {

    private static long answerOptionImpactId = 134L;

    public static org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto createAnswerOptionImpactDto(QuestionImpactDto questionImpactDto) {
        return new org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto(
            answerOptionImpactId++,
            1.0,
            questionImpactDto
        );
    }
}
