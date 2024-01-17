package application.port.out;

import application.domain.Plan;

import java.util.Map;
import java.util.UUID;

public interface LoadAdviceCalculationInfoPort {

    Plan load(UUID assessmentId, Map<Long, Long> targets);
}
