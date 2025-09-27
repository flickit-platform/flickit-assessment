package org.flickit.assessment.core.application.port.out.advicenarration;

import org.flickit.assessment.core.application.domain.AdviceNarration;

import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceNarrationPort {

    String loadNarration(UUID assessmentResultId);

    Optional<AdviceNarration> loadByAssessmentResultId(UUID assessmentResultId);

    boolean existsByAssessmentResultId(UUID assessmentResultId);
}
