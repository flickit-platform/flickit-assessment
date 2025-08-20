package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.core.application.domain.Space;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceMother {

    static long id = 0;

    public static Space createBasicSpace() {
        return createBasicSpaceWithOwnerId(UUID.randomUUID());
    }

    public static Space createBasicSpaceWithOwnerId(UUID ownerId) {
        return createSpaceWithTypeAndOwnerId(SpaceType.BASIC, ownerId);
    }

    public static Space createPremiumSpaceWithOwnerId(UUID ownerId) {
        return createSpaceWithTypeAndOwnerId(SpaceType.PREMIUM, ownerId);
    }

    private static Space createSpaceWithTypeAndOwnerId(SpaceType type, UUID ownerId){
        return new Space(++id,
            "title",
            ownerId,
            type,
            false,
            LocalDateTime.now());
    }

    public static Space createPremiumSpace() {
        return new Space(++id,
            "title",
            UUID.randomUUID(),
            SpaceType.PREMIUM,
            false,
            LocalDateTime.now());
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
            LocalDateTime.now());
    }

    public static Space createExpiredPremiumSpace(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.PREMIUM,
            false,
            LocalDateTime.MIN);
    }
}
