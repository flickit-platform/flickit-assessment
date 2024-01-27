package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import java.util.List;

public record CreateKitByDslRequestDto(String title,
                                       String summary,
                                       String about,
                                       boolean isPrivate,
                                       Long kitDslId,
                                       Long expertGroupId,
                                       List<Long> tagIds) {
}
