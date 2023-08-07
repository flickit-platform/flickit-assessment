package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.domain.calculate.AssessmentResult;

public interface UpdateCalculateResultPort {

    void updateCalculatedResult(AssessmentResult assessmentResult);
}
