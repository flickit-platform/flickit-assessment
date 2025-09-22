package org.flickit.assessment.advice.adapter.in.rest.advice;

import org.flickit.assessment.advice.application.domain.advice.QuestionRecommendation;

import java.util.List;

public record GenerateAdvicePlanResponseDto(List<QuestionRecommendation> items) {
}
