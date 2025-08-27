package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentPort {

    Optional<Assessment> loadById(UUID assessmentId);

    boolean isInDefaultSpace(UUID assessmentId);

    boolean isAssessmentSpaceMember(UUID assessmentId, UUID userId);

    /**
     * @throws ResourceNotFoundException if no assessment result found by the given id
     */
    ProgressResult progress(UUID assessmentId);

    record ProgressResult(UUID id, int answersCount, int questionsCount) {
    }
}
