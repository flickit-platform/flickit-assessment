package org.flickit.flickitassessmentcore.test.fixture.application;

import org.flickit.flickitassessmentcore.application.domain.Evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public class EvidenceMother {

    public static Evidence simpleEvidence() {
        return new Evidence(
            UUID.randomUUID(),
            "description",
            1L,
            UUID.randomUUID(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            false
        );
    }

}
