package org.flickit.assessment.users.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Space {

    private final Long id;
    private final String code;
    private final String title;
    private final SpaceType type;
    private final UUID ownerId;
    private final SpaceStatus status;
    private final LocalDateTime subscriptionExpiry;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
}
