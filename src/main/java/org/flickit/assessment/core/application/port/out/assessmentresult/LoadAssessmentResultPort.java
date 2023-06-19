package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.core.domain.AssessmentResult;

import java.util.UUID;

public interface LoadAssessmentResultPort {

    public AssessmentResult loadAssessmentResult(UUID resultId);
}
