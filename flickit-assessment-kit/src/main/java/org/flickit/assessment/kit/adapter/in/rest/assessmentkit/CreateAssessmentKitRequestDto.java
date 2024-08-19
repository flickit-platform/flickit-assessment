package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

public record CreateAssessmentKitRequestDto(String title,
                                            String summary,
                                            Long expertGroupId,
                                            String about,
                                            Boolean isPrivate) {
}
