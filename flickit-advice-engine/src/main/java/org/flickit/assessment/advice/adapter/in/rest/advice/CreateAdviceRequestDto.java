package org.flickit.assessment.advice.adapter.in.rest.advice;

import java.util.Map;

public record CreateAdviceRequestDto(Map<Long, Long> attributeLevelScores) {
}
