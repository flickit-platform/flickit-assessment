package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

public record CreateAssessmentRequestDto(String title,
                                         String description,
                                         Long assessmentKitId,
                                         Long colorId) {
}
