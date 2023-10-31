package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.core.application.domain.Assessment;

import java.util.Optional;
import java.util.UUID;

public interface GetAssessmentPort {

    Optional<Assessment> getAssessmentById(UUID assessmentId);
}
