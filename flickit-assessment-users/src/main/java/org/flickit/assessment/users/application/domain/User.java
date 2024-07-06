package org.flickit.assessment.users.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class User {

    private final UUID id;
    private final String email;
    private final String displayName;
    private final String bio;
    private final String linkedin;
    private final String picturePath;
}
