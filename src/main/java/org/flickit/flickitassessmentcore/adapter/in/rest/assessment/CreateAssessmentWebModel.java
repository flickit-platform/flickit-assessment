package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

public record CreateAssessmentWebModel(String title,
                                       String description,
                                       Long assessmentKitId,
                                       Long colorId) {
}
