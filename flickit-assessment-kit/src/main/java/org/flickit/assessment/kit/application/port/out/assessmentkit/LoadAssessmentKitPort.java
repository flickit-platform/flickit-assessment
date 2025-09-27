package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.domain.AssessmentKit;

public interface LoadAssessmentKitPort {

    AssessmentKit load(long kitId);

    AssessmentKit loadTranslated(long kitId);
}
