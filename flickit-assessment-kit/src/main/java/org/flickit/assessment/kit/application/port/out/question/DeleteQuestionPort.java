package org.flickit.assessment.kit.application.port.out.question;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DeleteQuestionPort {

    void delete(long questionId, long kitVersionId);

    void deleteQuestionAnswerRange(Param param);

    record Param(long answerRangeId,
                 long kitVersionId,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
