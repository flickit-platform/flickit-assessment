package org.flickit.assessment.core.adapter.in.rest.assessment;

import java.util.UUID;

public record GetAssessmentProgressResponseDto(UUID id, int answersCount, int questionsCount) {
}
