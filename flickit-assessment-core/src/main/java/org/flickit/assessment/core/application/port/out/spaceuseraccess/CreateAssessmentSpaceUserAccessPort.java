package org.flickit.assessment.core.application.port.out.spaceuseraccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentSpaceUserAccessPort {

    void persist(Param param);

    record Param(UUID assessmentId,
                 UUID userId,
                 UUID createdBy,
                 LocalDateTime creationTime) {
    }
}
