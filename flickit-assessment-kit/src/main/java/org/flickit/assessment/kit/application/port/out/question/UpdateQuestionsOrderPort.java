package org.flickit.assessment.kit.application.port.out.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateQuestionsOrderPort {

    void updateQuestionsOrder(Param param);

    record Param(long kitVersionId, List<QuestionOrder> orders, LocalDateTime lastModificationTime, UUID lastModifiedBy) {

        public record QuestionOrder(long questionId, int index) {}
    }
}
