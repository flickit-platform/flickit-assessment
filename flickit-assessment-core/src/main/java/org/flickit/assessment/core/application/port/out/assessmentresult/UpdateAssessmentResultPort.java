package org.flickit.assessment.core.application.port.out.assessmentresult;

import java.util.UUID;

public interface UpdateAssessmentResultPort {

    void updateKitVersionId(UUID assessmentResultId, Long kitVersionId);
}
