package org.flickit.assessment.core.adapter.in.rest.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAssessmentResponseDto(
    UUID id,
    String title,
    Space space,
    Kit kit,
    LocalDateTime creationTime,
    LocalDateTime lastModificationTime,
    User createdBy) {

    public record Space(long id, String title) {
    }

    public record Kit(long id, String title) {

    }

    record User(UUID id, String displayName) {
    }
}
