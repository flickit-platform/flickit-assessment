package org.flickit.assessment.kit.application.port.out.answerrange;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerRangePort {

    void update(Param param);

    record Param(
        long answerRangeId,
        long kitVersionId,
        String title,
        boolean reusable,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
