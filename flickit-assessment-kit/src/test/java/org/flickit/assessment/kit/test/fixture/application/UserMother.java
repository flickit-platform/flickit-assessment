package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMother {

    public static User userWithEmail(String email) {
        return new User(
            UUID.randomUUID(),
            email,
            "full name",
            "bio",
            LocalDateTime.now(),
            false,
            false,
            false,
            1L,
            1L
        );
    }
}
