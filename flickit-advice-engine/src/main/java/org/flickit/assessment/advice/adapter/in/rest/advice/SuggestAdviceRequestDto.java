package org.flickit.assessment.advice.adapter.in.rest.advice;

import java.util.Map;

public record SuggestAdviceRequestDto(Map<Long, Long> targets) {
}
