package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.application.domain.AssessmentMode;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentPort {

    UUID persist(Param param);

    record Param(String code,
                 String title,
                 String shortTitle,
                 Long assessmentKitId,
                 Long spaceId,
                 AssessmentMode mode,
                 LocalDateTime creationTime,
                 Long deletionTime,
                 boolean deleted,
                 UUID createdBy) {
    }
}
