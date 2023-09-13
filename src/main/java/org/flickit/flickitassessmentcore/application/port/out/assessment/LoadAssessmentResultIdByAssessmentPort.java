package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;

import java.util.Optional;
import java.util.UUID;

public interface LoadAssessmentResultIdByAssessmentPort {
    Optional<AssessmentResult> loadAssessmentResultByAssessmentId(UUID assessmentId);
}
