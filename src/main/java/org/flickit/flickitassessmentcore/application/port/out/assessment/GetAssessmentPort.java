package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface GetAssessmentPort {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    Assessment getAssessmentById(UUID assessmentId);
}
