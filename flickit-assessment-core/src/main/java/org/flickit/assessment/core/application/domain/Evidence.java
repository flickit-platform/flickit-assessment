package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Evidence {

    private final UUID id;
    private final String description;
    private final UUID createdById;
    private final UUID lastModifiedById;
    private final UUID assessmentId;
    private final long questionId;
    private final Integer type;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final boolean deleted;
}
