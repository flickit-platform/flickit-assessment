package org.flickit.flickitassessmentcore.application.port.out.evidence;

import java.util.UUID;

public interface AddEvidencePort {

    Result addEvidence(Param param);

    record Param(String description, Long createdById, UUID assessmentId, Long questionId) {
    }

    record Result(UUID id) {
    }
}
