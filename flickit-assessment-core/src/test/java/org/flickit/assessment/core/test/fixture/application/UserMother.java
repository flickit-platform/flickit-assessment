package org.flickit.assessment.core.test.fixture.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.core.application.domain.User;

import java.util.UUID;

public class UserMother {

    public static User createUser() {
        return new User(UUID.randomUUID(),
            RandomStringUtils.random(10, true, false),
            "test@test.com");
    }
}
