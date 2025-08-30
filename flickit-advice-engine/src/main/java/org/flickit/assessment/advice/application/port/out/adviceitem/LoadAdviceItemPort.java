package org.flickit.assessment.advice.application.port.out.adviceitem;

import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceItemPort {

    Optional<UUID> loadAssessmentIdById(UUID id);

    boolean existsByAssessmentResultId(UUID assessmentResultId);
}
