package org.flickit.flickitassessmentcore.test.fixture.adapter.dto;

import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;

public class AnswerOptionDtoMother {

    public static AnswerOptionDto createAnswerOptionDto(Long answerOptionId, QuestionDto question) {
        return new AnswerOptionDto(
            answerOptionId,
            question.id(),
            question.questionImpacts().stream()
                .map(AnswerOptionImpactDtoMother::createAnswerOptionImpactDto)
                .toList()
        );
    }
}
