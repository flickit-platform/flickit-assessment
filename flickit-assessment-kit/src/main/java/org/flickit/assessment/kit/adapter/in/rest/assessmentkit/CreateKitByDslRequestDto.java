package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import java.util.List;

public record CreateKitByDslRequestDto(Long kitDslId,
                                       boolean isPrivate,
                                       Long expertGroupId,
                                       String title,
                                       String summary,
                                       String about,
                                       List<Long> tagIds) {
}
