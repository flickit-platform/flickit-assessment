package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultPort {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId);
}
