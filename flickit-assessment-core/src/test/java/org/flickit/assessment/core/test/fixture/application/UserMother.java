package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMother {

    public static User user() {
        UUID id = UUID.randomUUID();
        return new User(
            id,
            "email" + id + "@mail.com",
            "name" + id,
            "bio",
            LocalDateTime.now(),
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.TRUE,
            1L,
            1L
        );
    }
}
