package org.flickit.assessment.core.test.fixture.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.core.application.domain.FullUser;

import java.util.UUID;

public class FullUserMother {

    public static FullUser createFullUser(String picturePath) {
        return new FullUser(UUID.randomUUID(),
            RandomStringUtils.randomAlphabetic(10),
            "test@test.com",
            picturePath);
    }
}
