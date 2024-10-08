package org.flickit.assessment.kit.application.port.out.questionimpact;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateQuestionImpactByDslPort {

    void updateByDsl(Param param);

    record Param(
        Long id,
        int weight,
        Long questionId,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy
    ) {
    }
}
