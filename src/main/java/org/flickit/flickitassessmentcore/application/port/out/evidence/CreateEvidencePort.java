package org.flickit.flickitassessmentcore.application.port.out.evidence;

import java.util.UUID;

public interface CreateEvidencePort {

    UUID persist(Param param);

    record Param(String description, Long createdById, UUID assessmentId, Long questionId) {
    }

}
