package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Builder
@FieldNameConstants
public record CreateKitByDslRequestDto(String title,
                                       String summary,
                                       String about,
                                       String lang,
                                       boolean isPrivate,
                                       Long kitDslId,
                                       Long expertGroupId,
                                       List<Long> tagIds) {
}
