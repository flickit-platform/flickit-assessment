package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;

import java.util.Map;
import java.util.UUID;

public interface CreateQuestionPort {

    Long persist(Param param);

    record Param(
        String code,
        String title,
        int index,
        String hint,
        Boolean mayNotBeApplicable,
        Boolean advisable,
        Long kitVersionId,
        Long questionnaireId,
        Long measureId,
        Long answerRangeId,
        Map<KitLanguage, QuestionTranslation> translations,
        UUID createdBy) {
    }
}
