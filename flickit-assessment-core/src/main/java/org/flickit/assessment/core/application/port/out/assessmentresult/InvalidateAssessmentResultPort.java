package org.flickit.assessment.core.application.port.out.assessmentresult;

import java.util.UUID;

public interface InvalidateAssessmentResultPort {

    void invalidateById(UUID assessmentResultId, Boolean isCalculateValid, Boolean isConfidenceValid);
}
