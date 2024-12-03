package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.User;

import java.util.UUID;

public class UserMother {

    public static User createUser(UUID id, String picturePath) {
        return new User(
            id,
            "email@email.com",
            "displayName",
            "bio",
            "linkedin",
            picturePath
        );
    }
}
