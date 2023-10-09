package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;

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
            LocalDateTime.now()
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
