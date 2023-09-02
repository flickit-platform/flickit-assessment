package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

public record SubmitAnswerRequestDto(Long questionnaireId, Long questionId, Long answerOptionId) {
}
