package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.core.application.domain.Space;

public class SpaceMother {

    private static int id = 0;

    public static Space createSpaceWithStatus(SpaceStatus status) {
        return new Space(++id,
            "Space " + id,
            status);
    }
}
