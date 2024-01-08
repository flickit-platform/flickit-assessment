package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

public record CreateKitByDslRequestDto( Long kitJsonDslId,
                                        String title,
                                        String summary,
                                        String about,
                                        String tags) {
}
