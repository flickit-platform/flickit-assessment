package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;


public interface CalculateMaturityLevelUseCase {

    AssessmentResult calculateMaturityLevel(CalculateMaturityLevelCommand command);

}
