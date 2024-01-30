package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.domain.advice.QuestionListItem;

import java.util.List;

public record SuggestAdviceResponseDto(List<QuestionListItem> questions) {
}
