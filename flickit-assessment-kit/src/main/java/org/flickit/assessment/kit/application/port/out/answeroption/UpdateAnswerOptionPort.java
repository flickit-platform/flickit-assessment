package org.flickit.assessment.kit.application.port.out.answeroption;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void updateAnswerOption(Param param);

    record Param(
        long answerOptionId,
        long kitVersionId,
        int index,
        String title,
        double value,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
