package org.flickit.assessment.core.adapter.in.rest.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAssessmentResponseDto(
    UUID assessmentId,
    String assessmentTitle,
    Long spaceId,
    Long kitId,
    LocalDateTime creationTime,
    AssessmentCreatorResponseDto createdBy) {

    record AssessmentCreatorResponseDto(UUID id,
                                        String displayName) {
    }
}
