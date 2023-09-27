package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.Assessment;

import java.util.Optional;
import java.util.UUID;

public interface GetAssessmentPort {

    Optional<Assessment> getAssessmentById(UUID assessmentId);
}
