package org.flickit.assessment.advice.application.port.out.assessmentadvice;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAdvicePort {

    UUID persist(Param param);

    record Param(UUID assessmentResultId,
                 UUID createdBy,
                 LocalDateTime creationTime,
                 LocalDateTime lastModificationTime) {
    }
}
