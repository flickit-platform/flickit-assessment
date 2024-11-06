package org.flickit.assessment.kit.application.port.out.answeroption;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void update(Param param);

    record Param(
        long answerOptionId,
        long kitVersionId,
        int index,
        String title,
        double value,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}

    void updateTitle(UpdateTitleParam param);

    record UpdateTitleParam(
        Long answerOptionId,
        Long kitVersionId,
        String title,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {
    }
}
