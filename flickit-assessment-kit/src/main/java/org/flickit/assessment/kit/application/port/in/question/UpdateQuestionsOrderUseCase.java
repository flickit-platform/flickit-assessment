package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateQuestionsOrderUseCase {

    void updateQuestionsOrder(Param params);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_QUESTIONS_ORDER_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_QUESTIONS_ORDER_ORDERS_NOT_NULL)
        List<QuestionOrder> orders;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<QuestionOrder> orders, UUID currentUserId) {

            this.kitVersionId = kitVersionId;
            this.orders = orders;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }

        @Value
        @EqualsAndHashCode(callSuper = false)
        public static class QuestionOrder extends SelfValidating<QuestionOrder> {

            @NotNull(message = UPDATE_QUESTIONS_ORDER_QUESTION_ID_NOT_NULL)
            Long questionId;

            @NotNull(message = UPDATE_QUESTIONS_ORDER_INDEX_NOT_NULL)
            Integer index;

            @Builder
            public QuestionOrder(Long questionId, Integer index) {
                this.questionId = questionId;
                this.index = index;
                this.validateSelf();
            }
        }
    }
}
