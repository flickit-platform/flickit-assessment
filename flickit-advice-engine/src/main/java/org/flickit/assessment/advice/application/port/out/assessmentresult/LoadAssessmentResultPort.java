package org.flickit.assessment.advice.application.port.out.assessmentresult;

import org.flickit.assessment.advice.application.domain.AssessmentResult;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultPort {

    Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId);

    record AssessmentResult(UUID id, String assessmentTitle, long kitVersionId, LocalDateTime lastCalculationTime) {}
}
