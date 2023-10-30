package org.flickit.assessment.core.adapter.in.rest.assessment;

import java.util.UUID;

record GetAssessmentProgressResponseDto(UUID id, Integer allAnswersCount) {
}
