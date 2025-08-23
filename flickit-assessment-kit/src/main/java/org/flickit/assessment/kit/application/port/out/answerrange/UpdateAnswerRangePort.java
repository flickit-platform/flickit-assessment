package org.flickit.assessment.kit.application.port.out.answerrange;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface UpdateAnswerRangePort {

    void update(Param param);

    record Param(
        long answerRangeId,
        long kitVersionId,
        String title,
        String code,
        boolean reusable,
        Map<KitLanguage, AnswerRangeTranslation> translations,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {}
}
