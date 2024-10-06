package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

public record CreateAssessmentKitRequestDto(String title,
                                            String summary,
                                            String about,
                                            Boolean isPrivate,
                                            Long expertGroupId) {
}
