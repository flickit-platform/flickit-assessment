package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.core.application.domain.AssessmentResult;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultPort {

    Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId);
}
