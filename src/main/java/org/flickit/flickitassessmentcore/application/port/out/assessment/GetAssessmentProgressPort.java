package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface GetAssessmentProgressPort {

    /**
     * @throws ResourceNotFoundException if no assessment result found by the given id
     */
    Result getAssessmentProgressById(UUID assessmentId);

    record Result(UUID id, int allAnswersCount) {
    }
}
