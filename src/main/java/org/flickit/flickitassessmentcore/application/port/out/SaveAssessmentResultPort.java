package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

public interface SaveAssessmentResultPort {

    public void saveAssessmentResult(AssessmentResult assessmentResult);
}
