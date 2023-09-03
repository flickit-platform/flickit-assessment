package org.flickit.flickitassessmentcore.application.port.out.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateEvidencePort {

    UUID persist(Param param);

    record Param(String description,
                 LocalDateTime creationTime,
                 LocalDateTime lastModificationTime,
                 Long createdById,
                 UUID assessmentId,
                 Long questionId) {
    }
}
