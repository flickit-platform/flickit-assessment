package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.QuestionnaireOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateQuestionnairePort {

    void update(Param param);

    void updateIndexes(Long kitVersionId, List<QuestionnaireOrder> orders);

    record Param(
        long id,
        long kitVersionId,
        String title,
        int index,
        String description,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy
    ) {
    }
}
