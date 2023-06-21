package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;


public interface CalculateMaturityLevelUseCase {

    AssessmentResult calculateMaturityLevel(CalculateMaturityLevelCommand command);

}
