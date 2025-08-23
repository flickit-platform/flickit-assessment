package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Builder
@FieldNameConstants
public record GrantUserAccessToKitRequestDto(UUID userId) {
}
