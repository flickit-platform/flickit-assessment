package org.flickit.assessment.advice.application.port.out;

import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase.AttributeLevelTarget;

import java.util.List;
import java.util.UUID;

public interface LoadAdviceCalculationInfoPort {

    Plan loadAdviceCalculationInfo(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets);
}
