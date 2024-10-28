package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_OPTION_ANSWER_OPTION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL;

public interface DeleteAnswerOptionUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_ANSWER_OPTION_ANSWER_OPTION_ID_NOT_NULL)
        Long answerOptionId;

        @NotNull(message = DELETE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long answerOptionId, Long kitVersionId, UUID currentUserId) {
            this.answerOptionId = answerOptionId;
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
