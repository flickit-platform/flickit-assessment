package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_QUESTION_ID_NOT_NULL;

public interface DeleteQuestionUseCase {

    void deleteQuestion(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_QUESTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = DELETE_QUESTION_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long questionId, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.questionId = questionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
