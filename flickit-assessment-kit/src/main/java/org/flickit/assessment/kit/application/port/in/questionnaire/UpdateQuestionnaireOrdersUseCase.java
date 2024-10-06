package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateQuestionnaireOrdersUseCase {

    void changeOrders(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_QUESTIONNAIRE_ORDERS_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotEmpty(message = UPDATE_QUESTIONNAIRE_ORDERS_ORDERS_NOT_NULL)
        List<QuestionnaireParam> orders;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<QuestionnaireParam> orders, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.orders = orders;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class QuestionnaireParam extends SelfValidating<QuestionnaireParam> {

        @NotNull(message = UPDATE_QUESTIONNAIRE_ORDERS_QUESTIONNAIRE_ID_NOT_NULL)
        Long id;

        @NotNull(message = UPDATE_QUESTIONNAIRE_ORDERS_QUESTIONNAIRE_INDEX_NOT_NULL)
        @Min(value = 1, message = UPDATE_QUESTIONNAIRE_ORDERS_QUESTIONNAIRE_INDEX_MIN)
        Integer index;

        @Builder
        public QuestionnaireParam(Long id, Integer index) {
            this.id = id;
            this.index = index;
            this.validateSelf();
        }
    }
}
