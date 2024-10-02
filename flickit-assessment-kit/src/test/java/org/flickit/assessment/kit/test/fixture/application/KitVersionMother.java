package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class KitVersionMother {

    private static long id = 123L;

    public static KitVersion createKitVersion(AssessmentKit kit) {
        return new KitVersion(
            id++,
            kit,
            KitVersionStatus.UPDATING,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    }
}
