package org.flickit.flickitassessmentcore.adapter.in.web.Assessment;

public record CreateAssessmentWebModel(String title,
                                       String description,
                                       Long assessmentKitId,
                                       Long colorId) {
}
