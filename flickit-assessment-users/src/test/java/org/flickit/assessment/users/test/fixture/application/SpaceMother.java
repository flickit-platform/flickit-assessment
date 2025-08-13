package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceStatus;

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
            SpaceStatus.ACTIVE,
            null,
            false,
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
            SpaceStatus.ACTIVE,
            LocalDateTime.now().minusMonths(3),
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }

    public static Space inactiveSpace(UUID ownerId) {
        return new Space(
            id++,
            "Code" + id,
            "Title",
            SpaceType.PREMIUM,
            ownerId,
            SpaceStatus.INACTIVE,
            LocalDateTime.now().minusMonths(3),
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }

    public static Space defaultSpace(UUID ownerId) {
        return new Space(
            id++,
            "Code" + id,
            "Title",
            SpaceType.BASIC,
            ownerId,
            SpaceStatus.ACTIVE,
            null,
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }
}
