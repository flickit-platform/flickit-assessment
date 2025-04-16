package org.flickit.assessment.kit.application.port.out.answeroption;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CreateAnswerOptionPort {

    long persist(Param param);

    void persistAll(List<Param> params);

    record Param(
        String title,
        Integer index,
        Long answerRangeId,
        double value,
        Map<KitLanguage, AnswerOptionTranslation> translation,
        Long kitVersionId,
        UUID createdBy) {
    }
}
