package org.flickit.assessment.core.application.port.out.assessmentresult;

import java.util.UUID;

public interface InvalidateAssessmentResultCalculatePort {

    void invalidateCalculateById(UUID assessmentResultId);
}
