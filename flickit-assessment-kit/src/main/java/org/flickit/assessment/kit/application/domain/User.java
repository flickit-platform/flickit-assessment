package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class User {

    private final UUID id;
    private final String email;
    private final String displayName;
    private final String bio;
    private final String linkedin;
    private final String picture;
    private final Long defaultSpaceId;
}
