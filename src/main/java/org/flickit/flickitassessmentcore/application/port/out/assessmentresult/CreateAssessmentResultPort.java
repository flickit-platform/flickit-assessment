package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentResultPort {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    UUID persist(Param param);

    record Param(UUID assessmentId, LocalDateTime lastModificationTime, boolean isValid) {
    }
}
