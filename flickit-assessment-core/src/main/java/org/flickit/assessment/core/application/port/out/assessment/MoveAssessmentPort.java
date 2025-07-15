package org.flickit.assessment.core.application.port.out.assessment;

import java.util.UUID;

public interface MoveAssessmentPort {

    void moveAssessment(UUID assessmentId, long spaceId);
}
