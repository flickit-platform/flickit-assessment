package org.flickit.assessment.kit.application.port.out.questionimpact;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateQuestionImpactPort {

    void update(Param param);

    record Param(
        long kitVersionId,
        long questionImpactId,
        int weight,
        long attributeId,
        long maturityLevelId,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
