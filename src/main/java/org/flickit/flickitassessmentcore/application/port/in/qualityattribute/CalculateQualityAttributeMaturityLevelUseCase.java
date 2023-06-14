package org.flickit.flickitassessmentcore.application.port.in.qualityattribute;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;


public interface CalculateQualityAttributeMaturityLevelUseCase {

    MaturityLevel calculateQualityAttributeMaturityLevel(CalculateQAMaturityLevelCommand command);

}
