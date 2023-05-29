package org.flickit.flickitassessmentcore.application.port.in;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.Set;
import java.util.UUID;

public interface CalculateQualityAttributeMaturityLevelUseCase {

    MaturityLevel calculateQualityAttributeMaturityLevel(Set<AssessmentResult> assessmentResults, Long qualityAttributeId);

}
