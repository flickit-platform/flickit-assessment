package org.flickit.assessment.kit.application.port.out.answeroption;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void updateAnswerOption(Param param);

    record Param(
        long kitVersionId,
        long answerOptionId,
        int index,
        String title,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy
        ) {}
}
