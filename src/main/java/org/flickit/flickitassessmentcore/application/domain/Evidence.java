package org.flickit.flickitassessmentcore.application.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Evidence {

    private final UUID id;
    private final String description;
    private final long createdById;
    private final UUID assessmentId;
    private final long questionId;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final boolean deleted;
}
