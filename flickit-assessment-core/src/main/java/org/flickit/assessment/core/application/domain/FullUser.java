package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class FullUser {

    private final UUID id;
    private final String displayName;
    private final String email;
    private final String picturePath;
}
