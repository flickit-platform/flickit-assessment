package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Evidence;

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


    public static EvidenceListItem evidenceListItem(UUID assessmentId) {
        return new EvidenceListItem(
            UUID.randomUUID(),
            "description",
            1L,
            assessmentId,
            LocalDateTime.now()
        );
    }
}
