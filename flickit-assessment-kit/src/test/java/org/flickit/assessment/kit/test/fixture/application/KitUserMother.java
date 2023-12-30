package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.KitUser;

import java.util.UUID;

public class KitUserMother {

    public static KitUser simpleKitUser(Long kitId, UUID userId) {
        return new KitUser(kitId, userId);
    }
}
