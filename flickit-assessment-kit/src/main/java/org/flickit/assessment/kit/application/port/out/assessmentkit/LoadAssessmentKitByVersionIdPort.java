package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.domain.AssessmentKit;

public interface LoadAssessmentKitByVersionIdPort {

    AssessmentKit loadByVersionId(Long kitVersionId);
}
