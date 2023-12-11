package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.User;

import java.time.LocalDateTime;

public class UserMother {

    private static Long id = 134L;

    public static User simpleUser() {
        Long userId = id++;
        return new User(
            userId,
            LocalDateTime.now(),
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
            "User" + userId + "@mail.com",
            1L,
            "user" + userId,
            "bio",
            "linkedin",
            "picture",
            1L
        );
    }
}
