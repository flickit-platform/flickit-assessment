package org.flickit.assessment.kit.application.port.out.answeroptionimpact;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerOptionImpactPort {

    void update(Param param);

    record Param(Long id, Double value, LocalDateTime lastModificationTime, UUID lastModifiedBy) {
    }
}
