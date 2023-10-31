package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.core.application.domain.AssessmentResult;

import java.util.UUID;

public interface LoadCalculateInfoPort {

    AssessmentResult load(UUID assessmentId);
}
