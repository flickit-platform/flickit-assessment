package org.flickit.assessment.kit.application.port.out.answeroption;

import org.flickit.assessment.kit.application.domain.AnswerOptionOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void update(Param param);

    void updateOrders(List<AnswerOptionOrder> answerOptionOrders, Long kitVersionId, UUID lastModifiedBy);

    record Param(
        Long id,
        Long kitVersionId,
        String title,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
