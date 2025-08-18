package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.core.application.domain.Space;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceMother {

    static long id = 0;

    public static Space createBasicSpace() {
        return new Space(++id,
            "title",
            UUID.randomUUID(),
            SpaceType.BASIC,
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
        return new Space(++id,
            "title",
            UUID.randomUUID(),
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
