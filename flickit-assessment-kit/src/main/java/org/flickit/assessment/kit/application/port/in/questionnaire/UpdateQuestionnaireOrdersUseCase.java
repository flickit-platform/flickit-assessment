package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.QuestionnaireOrder;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_QUESTIONNAIRE_ORDERS_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_QUESTIONNAIRE_ORDERS_ORDERS_NOT_NULL;

public interface UpdateQuestionnaireOrdersUseCase {

    void changeOrders(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_QUESTIONNAIRE_ORDERS_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_QUESTIONNAIRE_ORDERS_ORDERS_NOT_NULL)
        List<QuestionnaireOrder> orders;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<QuestionnaireOrder> orders, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.orders = orders;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
