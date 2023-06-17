package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

public record CreateAssessmentRequestDto(Long spaceId, String title, Long assessmentKitId, Long colorId) {
}
