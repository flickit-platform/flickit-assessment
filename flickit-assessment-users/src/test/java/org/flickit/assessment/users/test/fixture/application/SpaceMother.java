package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.domain.Space;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceMother {

    private static Long id = 134L;

    public static Space basicSpace(UUID ownerId) {
        return new Space(
            id++,
            "Code" + id,
            "Title",
            SpaceType.BASIC,
            ownerId,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }

    public static Space premiumSpace(UUID ownerId) {
        return new Space(
            id++,
            "Code" + id,
            "Title",
            SpaceType.PREMIUM,
            ownerId,
            LocalDateTime.now().minusMonths(3),
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
