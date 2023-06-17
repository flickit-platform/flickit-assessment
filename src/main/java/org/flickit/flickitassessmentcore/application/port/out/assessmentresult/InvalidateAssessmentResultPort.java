package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import java.util.UUID;

public interface InvalidateAssessmentResultPort {

    void invalidateAssessmentResultById(UUID id);
}
