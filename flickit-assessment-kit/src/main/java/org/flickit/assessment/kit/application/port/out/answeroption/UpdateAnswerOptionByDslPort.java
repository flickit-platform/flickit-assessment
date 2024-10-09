package org.flickit.assessment.kit.application.port.out.answeroption;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerOptionByDslPort {

    void updateByDsl(Param param);

    record Param(
        Long id,
        Long kitVersionId,
        String title,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
