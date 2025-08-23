package org.flickit.assessment.advice.application.port.out.assessmentresult;

import org.flickit.assessment.advice.application.domain.AssessmentResult;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultPort {

    Optional<AssessmentResult> loadById(UUID assessmentResultId);

    Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId);
}
