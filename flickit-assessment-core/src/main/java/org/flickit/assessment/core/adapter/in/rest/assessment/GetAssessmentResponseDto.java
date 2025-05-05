package org.flickit.assessment.core.adapter.in.rest.assessment;

import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result.Language;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAssessmentResponseDto(
    UUID id,
    String title,
    String shortTitle,
    SpaceResponseDto space,
    Long kitCustomId,
    KitResponseDto kit,
    LocalDateTime creationTime,
    LocalDateTime lastModificationTime,
    UserResponseDto createdBy,
    MaturityLevel maturityLevel,
    boolean isCalculateValid,
    Language language,
    boolean manageable,
    boolean viewable) {

    record SpaceResponseDto(long id, String title) {
    }

    record KitResponseDto(long id, String title) {
    }

    record UserResponseDto(UUID id, String displayName) {
    }
}
