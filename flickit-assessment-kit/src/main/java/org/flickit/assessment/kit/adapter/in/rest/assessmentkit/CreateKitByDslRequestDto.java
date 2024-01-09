package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

public record CreateKitByDslRequestDto( Long kitJsonDslId,
                                        boolean isPrivate,
                                        Long expertGroupId,
                                        String title,
                                        String summary,
                                        String about,
                                        String[] tagIds) {
}
