package org.flickit.assessment.advice.application.port.out.assessmentresult;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultIdPort {

    Optional<UUID> loadByAssessmentId(UUID assessmentId);
}
