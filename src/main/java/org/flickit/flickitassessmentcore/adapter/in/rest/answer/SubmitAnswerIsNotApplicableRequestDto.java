package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

public record SubmitAnswerIsNotApplicableRequestDto(Long questionnaireId, Long questionId, Boolean isNotApplicable) {
}
