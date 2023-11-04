package org.flickit.assessment.core.adapter.in.rest.answer;

public record SubmitAnswerRequestDto(Long questionnaireId, Long questionId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable) {
}
