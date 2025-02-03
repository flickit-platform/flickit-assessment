package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.core.application.domain.Space;

import java.time.LocalDateTime;
import java.util.UUID;

public class SpaceMother {

    static long id = 0;

    public static Space createPersonalSpaceWithOwnerId(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.PERSONAL,
            LocalDateTime.now());
    }

    public static Space createPremiumExpiredSpace(UUID ownerId) {
        return new Space(++id,
            "title",
            ownerId,
            SpaceType.PREMIUM,
            LocalDateTime.MIN);
    }
}
