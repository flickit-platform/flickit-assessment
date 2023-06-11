package org.flickit.flickitassessmentcore.application.port.in;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;


public interface CalculateQualityAttributeMaturityLevelUseCase {

    MaturityLevel calculateQualityAttributeMaturityLevel(CalculateQAMaturityLevelCommand command);

}
