package org.flickit.assessment.common.application.port.out;

import java.util.UUID;

public interface ValidateAssessmentResultPort {

    void validate(UUID assessmentId);
}
