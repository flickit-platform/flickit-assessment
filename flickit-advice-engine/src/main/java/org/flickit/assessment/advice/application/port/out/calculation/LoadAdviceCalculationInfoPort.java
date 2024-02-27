package org.flickit.assessment.advice.application.port.out.calculation;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;

import java.util.List;
import java.util.UUID;

public interface LoadAdviceCalculationInfoPort {

    Plan loadAdviceCalculationInfo(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets);
}
