package org.flickit.assessment.core.application.port.out.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentPort {

    UUID persist(Param param);

    record Param(String code,
                 String title,
                 Long assessmentKitId,
                 Long spaceId,
                 LocalDateTime creationTime,
                 Long deletionTime,
                 boolean deleted,
                 UUID createdBy) {
    }
}
