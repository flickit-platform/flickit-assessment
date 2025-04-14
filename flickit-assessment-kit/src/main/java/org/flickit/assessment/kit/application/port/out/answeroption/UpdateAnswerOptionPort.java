package org.flickit.assessment.kit.application.port.out.answeroption;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface UpdateAnswerOptionPort {

    void update(Param param);

    record Param(
        long answerOptionId,
        long kitVersionId,
        int index,
        String title,
        double value,
        Map<KitLanguage, AnswerOptionTranslation> translations,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {
    }

    void updateTitle(UpdateTitleParam param);

    record UpdateTitleParam(
        Long answerOptionId,
        Long kitVersionId,
        String title,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {
    }
}
