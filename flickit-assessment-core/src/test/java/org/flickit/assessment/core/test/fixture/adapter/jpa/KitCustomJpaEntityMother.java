package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class KitCustomJpaEntityMother {

    private static long id = 223L;

    public static KitCustomJpaEntity createKitCustom(String customData) {
        return new KitCustomJpaEntity(id++,
            123L,
            "custom-title",
            "custom-title",
            customData,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    }
}
