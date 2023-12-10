package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public class EvidenceMother {

    public static Evidence simpleEvidence() {
        return new Evidence(
            UUID.randomUUID(),
            "description",
            UUID.randomUUID(),
            UUID.randomUUID(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
    }

}
