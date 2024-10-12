package org.flickit.assessment.core.application.port.out.assessmentresult;

import java.util.UUID;

public interface InvalidateAssessmentResultConfidencePort {

    void invalidateConfidence(UUID assessmentResultId);
}
