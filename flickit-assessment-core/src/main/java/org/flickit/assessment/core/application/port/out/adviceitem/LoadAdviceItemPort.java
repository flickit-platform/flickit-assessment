package org.flickit.assessment.core.application.port.out.adviceitem;

import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceItemPort {

    Optional<UUID> loadAssessmentIdById(UUID id);

    boolean existsByAssessmentResultId(UUID assessmentResultId);
}
