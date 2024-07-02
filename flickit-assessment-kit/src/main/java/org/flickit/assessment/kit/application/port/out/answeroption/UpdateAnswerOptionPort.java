package org.flickit.assessment.kit.application.port.out.answeroption;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void update(Param param);

    record Param(
        Long id,
        Long kitVersionId,
        String title,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
