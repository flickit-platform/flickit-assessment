package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.core.application.domain.Space;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceMother {

    private static int id = 0;

    public static Space createBasicSpace() {
        return createBasicSpaceWithOwnerId(UUID.randomUUID());
    }

    public static Space createBasicSpaceWithOwnerId(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.BASIC,
            false,
            LocalDateTime.now(),
            SpaceStatus.ACTIVE);
    }

    public static Space createPremiumSpace() {
        return createPremiumSpaceWithOwnerId(UUID.randomUUID());
    }

    public static Space createPremiumSpaceWithOwnerId(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.PREMIUM,
            false,
            LocalDateTime.now(),
            SpaceStatus.ACTIVE);
    }

    public static Space createSpaceWithStatus(SpaceStatus status) {
        return new Space(++id,
            "title",
            UUID.randomUUID(),
            SpaceType.PREMIUM,
            false,
            LocalDateTime.now(),
            status);
    }

    public static Space createDefaultSpace() {
        return createDefaultSpaceWithOwnerId(UUID.randomUUID());
    }

    public static Space createDefaultSpaceWithOwnerId(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.BASIC,
            true,
            LocalDateTime.now(),
            SpaceStatus.ACTIVE);
    }

    public static Space createExpiredPremiumSpace(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.PREMIUM,
            false,
            LocalDateTime.MIN,
            SpaceStatus.ACTIVE);
    }
}
