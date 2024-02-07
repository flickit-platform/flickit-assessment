package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentSpacePort {

    Optional<Long> loadAssessmentSpaceId(UUID assessmentId);
}
