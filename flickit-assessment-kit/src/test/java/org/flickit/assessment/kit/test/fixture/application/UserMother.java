package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.User;

import java.util.UUID;

public class UserMother {

    public static User simpleUser() {
        UUID userId = UUID.randomUUID();
        return new User(
            userId,
            "User" + userId + "@mail.com",
            "user" + userId,
            "bio",
            "linkedin",
            "picture" + userId,
            1L
        );
    }
}
