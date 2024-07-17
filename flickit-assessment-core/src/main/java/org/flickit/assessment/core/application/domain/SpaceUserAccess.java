package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SpaceUserAccess {

    private final long spaceId;
    private final UUID userId;
    private final UUID createdBy;
    private final LocalDateTime creationTime;
}
