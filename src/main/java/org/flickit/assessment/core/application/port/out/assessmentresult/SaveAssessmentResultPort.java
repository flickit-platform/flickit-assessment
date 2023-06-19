package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.core.domain.AssessmentResult;

public interface SaveAssessmentResultPort {

    public AssessmentResult saveAssessmentResult(AssessmentResult assessmentResult);
}
