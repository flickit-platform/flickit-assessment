package org.flickit.assessment.core.application.port.out.assessment;

import java.util.UUID;

public interface CheckAssessmentInDefaultSpacePort {

    boolean isAssessmentInDefaultSpace(UUID assessmentId);
}
