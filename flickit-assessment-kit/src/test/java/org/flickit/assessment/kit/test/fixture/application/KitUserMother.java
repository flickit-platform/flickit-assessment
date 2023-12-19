package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.KitUser;

import java.util.UUID;

public class KitUserMother {

    private static Long id = 134L;

    public static KitUser simpleKitUser() {
        return new KitUser(id++, UUID.randomUUID());
    }
}
