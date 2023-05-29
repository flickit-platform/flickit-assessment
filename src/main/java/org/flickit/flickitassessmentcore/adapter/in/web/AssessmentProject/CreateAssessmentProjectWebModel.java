package org.flickit.flickitassessmentcore.adapter.in.web.AssessmentProject;

public record CreateAssessmentProjectWebModel(String title,
                                              String description,
                                              Long assessmentKitId,
                                              Long colorId,
                                              Long spaceId) {
}
