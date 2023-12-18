package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.User;

import java.util.UUID;

public class UserMother {

    public static User userWithId(UUID id) {
        return new User(
            id,
            "user@email.com",
            "full name",
            "bio",
            "linkedin",
            "picture",
            1L
        );
    }
}
