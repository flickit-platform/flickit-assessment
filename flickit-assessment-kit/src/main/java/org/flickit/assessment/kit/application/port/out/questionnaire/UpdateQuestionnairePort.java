package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateQuestionnairePort {

    void update(Param param);

    record Param(
        long id,
        long kitVersionId,
        String title,
        String code,
        int index,
        String description,
        Map<KitLanguage, QuestionnaireTranslation> translations,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {
    }

    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(List<QuestionnaireOrder> orders,
                            long kitVersionId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {
        public record QuestionnaireOrder(long questionnaireId, int index) {
        }
    }
}
