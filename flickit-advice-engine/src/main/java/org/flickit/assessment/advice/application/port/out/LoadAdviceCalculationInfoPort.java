package org.flickit.assessment.advice.application.port.out;

import org.flickit.assessment.advice.application.domain.Plan;

import java.util.Map;
import java.util.UUID;

public interface LoadAdviceCalculationInfoPort {

    Plan load(UUID assessmentId, Map<Long, Long> targets);
}
