package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

public interface SaveAssessmentResultPort {

    public AssessmentResult saveAssessmentResult(AssessmentResult assessmentResult);
}
