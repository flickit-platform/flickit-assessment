package org.flickit.assessment.core.application.port.out.advicenarration;

import org.flickit.assessment.core.application.domain.AdviceNarration;

import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceNarrationPort {

    String load(UUID assessmentResultId);

    Optional<AdviceNarration> loadAdviceNarration(UUID assessmentResultId);
}
