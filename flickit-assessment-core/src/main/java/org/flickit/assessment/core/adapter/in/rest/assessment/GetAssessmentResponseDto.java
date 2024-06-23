package org.flickit.assessment.core.adapter.in.rest.assessment;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAssessmentResponseDto(
    UUID id,
    String title,
    SpaceResponseDto space,
    KitResponseDto kit,
    LocalDateTime creationTime,
    LocalDateTime lastModificationTime,
    UserResponseDto createdBy,
    MaturityLevel maturityLevel,
    boolean isCalculateValid) {

    record SpaceResponseDto(long id, String title) {
    }

    record KitResponseDto(long id, String title) {
    }

    record UserResponseDto(UUID id, String displayName) {
    }
}
