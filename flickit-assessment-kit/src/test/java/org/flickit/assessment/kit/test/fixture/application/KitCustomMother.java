package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.KitCustom;
import org.flickit.assessment.kit.application.domain.KitCustomData;

import java.time.LocalDateTime;
import java.util.UUID;

public class KitCustomMother {

    private static long id = 122;

    public static KitCustom simpleKitCustom(long kitId, KitCustomData customData) {
        return new KitCustom(
            id++,
            kitId,
            "custom N" + id,
            customData,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    }
}
