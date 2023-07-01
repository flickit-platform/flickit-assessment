package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import java.util.UUID;

public interface InvalidateAssessmentResultPort {

    void invalidateById(UUID id);
}
