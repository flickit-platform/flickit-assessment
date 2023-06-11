package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

public record CreateAssessmentRequestDto(String title,
                                         Long assessmentKitId,
                                         Long colorId) {
}
