package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.util.UUID;

public interface GetAssessmentProgressPort {

    /**
     * @throws ResourceNotFoundException if no assessment result found by the given id
     */
    Result getProgress(UUID assessmentId);

    record Result(UUID id, int answersCount, int questionsCount) {
    }
}
