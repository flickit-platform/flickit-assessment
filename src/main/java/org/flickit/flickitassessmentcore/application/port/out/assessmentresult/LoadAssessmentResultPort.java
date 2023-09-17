package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultPort {
    Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId);
}
