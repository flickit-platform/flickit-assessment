package org.flickit.assessment.core.application.port.in.assessmentresult;

import org.flickit.assessment.core.domain.AssessmentResult;


public interface CalculateMaturityLevelUseCase {

    AssessmentResult calculateMaturityLevel(CalculateMaturityLevelCommand command);

}
