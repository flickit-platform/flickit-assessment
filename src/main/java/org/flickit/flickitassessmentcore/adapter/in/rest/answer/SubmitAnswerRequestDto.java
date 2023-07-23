package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

public record SubmitAnswerRequestDto(Long questionId, Long questionnaireId, Long answerOptionId) {
}
