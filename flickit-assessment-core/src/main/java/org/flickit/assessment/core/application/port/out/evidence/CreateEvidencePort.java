package org.flickit.assessment.core.application.port.out.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateEvidencePort {

    UUID persist(Param param);

    record Param(String description,
                 LocalDateTime creationTime,
                 LocalDateTime lastModificationTime,
                 UUID createdById,
                 UUID assessmentId,
                 Long questionId,
                 Integer type) {
    }
}
