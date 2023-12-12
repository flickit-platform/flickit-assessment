package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.application.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentPort {

    UUID persist(Param param);

    record Param(String code,
                 String title,
                 Long assessmentKitId,
                 Integer colorId,
                 Long spaceId,
                 LocalDateTime creationTime,
                 LocalDateTime lastModificationTime,
                 Long deletionTime,
                 boolean deleted,
                 User createdBy) {
    }
}
