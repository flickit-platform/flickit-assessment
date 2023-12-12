package org.flickit.assessment.core.adapter.in.rest.answer;

import java.util.UUID;

public record SubmitAnswerRequestDto(Long questionnaireId, Long questionId, Long answerOptionId, Integer confidenceLevelId, Boolean isNotApplicable, UUID createdBy) {
}
