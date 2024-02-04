package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.domain.advice.QuestionListItem;

import java.util.List;

public record CreateAdviceResponseDto(List<QuestionListItem> questions) {
}
