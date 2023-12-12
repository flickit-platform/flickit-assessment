package org.flickit.assessment.core.adapter.in.rest.assessment;

import java.util.UUID;

public record CreateAssessmentRequestDto(Long spaceId, String title, Long assessmentKitId, Integer colorId, UUID createdBy) {
}
