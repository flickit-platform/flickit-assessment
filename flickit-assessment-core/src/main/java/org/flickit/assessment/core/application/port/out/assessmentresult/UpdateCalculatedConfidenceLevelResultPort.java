package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.core.application.domain.AssessmentResult;

public interface UpdateCalculatedConfidenceLevelResultPort {

    void updateCalculatedConfidenceLevelResult(AssessmentResult assessmentResult);
}
