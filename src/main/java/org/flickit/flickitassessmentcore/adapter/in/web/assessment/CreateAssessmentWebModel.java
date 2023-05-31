package org.flickit.flickitassessmentcore.adapter.in.web.assessment;

public record CreateAssessmentWebModel(String title,
                                       String description,
                                       Long assessmentKitId,
                                       Long colorId) {
}
