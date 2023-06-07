package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentPort {
    UUID persist(Param param);

    record Param(String title,
                 String description,
                 Long assessmentKitId,
                 Long colorId,
                 Long spaceId,
                 String code,
                 LocalDateTime creationTime,
                 LocalDateTime lastModificationDate) {

    }
}
