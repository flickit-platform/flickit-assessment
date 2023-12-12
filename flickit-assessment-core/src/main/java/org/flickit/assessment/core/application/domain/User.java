package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class User {

    private final UUID id;
    private final String email;
    private final String displayName;
    private final String bio;
    private final LocalDateTime lastLogin;
    private final boolean isSuperUser;
    private final boolean isStaff;
    private final boolean isActive;
    private final Long currentSpaceId;
    private final Long defaultSpaceId;
}
