package org.flickit.assessment.core.application.port.out.assessmentkit;

import org.flickit.assessment.kit.domain.AssessmentKit;

public interface LoadAssessmentKitInfoPort {

    AssessmentKit load(Long kitId);
}
