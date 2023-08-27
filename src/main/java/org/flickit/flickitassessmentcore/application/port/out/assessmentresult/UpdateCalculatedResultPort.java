package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;

public interface UpdateCalculatedResultPort {

    void updateCalculatedResult(AssessmentResult assessmentResult);
}
