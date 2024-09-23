package org.flickit.assessment.advice.application.port.out.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;

import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceNarrationPort {

    Optional<AdviceNarration> loadByAssessmentResultId(UUID assessmentResultId);
}
