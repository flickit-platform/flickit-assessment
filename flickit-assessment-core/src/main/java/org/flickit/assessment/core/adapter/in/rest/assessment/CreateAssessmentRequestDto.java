package org.flickit.assessment.core.adapter.in.rest.assessment;

public record CreateAssessmentRequestDto(Long spaceId, String title, Long assessmentKitId) {
}
