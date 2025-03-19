package org.flickit.assessment.core.application.port.out.assessmentkit;

import org.flickit.assessment.core.application.domain.AssessmentKit;

import java.util.Optional;

public interface LoadAssessmentKitPort {

    Optional<AssessmentKit> loadAssessmentKit(long kitId);
}
