package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAssessmentResultPort {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    UUID persist(Param param);

    record Param(UUID assessmentId, long kitVersionId, LocalDateTime lastModificationTime, boolean isCalculateValid, boolean isConfidenceValid) {
    }
}
