package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class User {

    private final Long id;
    private final LocalDateTime lastLogin;
    private final Boolean isSuperuser;
    private final Boolean isStaff;
    private final Boolean isActive;
    private final String email;
    private final Long currentSpaceId;
    private final String displayName;
    private final String bio;
    private final String linkedin;
    private final String picture;
    private final Long defaultSpaceId;
}
