package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateAnswerOptionUseCase {

    void updateAnswerOption(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_ANSWER_OPTION_ANSWER_OPTION_ID_NOT_NULL)
        Long answerOptionId;

        @NotNull(message = UPDATE_ANSWER_OPTION_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = UPDATE_ANSWER_OPTION_TITLE_NOT_NULL)
        @Size(min = 3, message = UPDATE_ANSWER_OPTION_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_ANSWER_OPTION_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long answerOptionId, Integer index, String title, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.answerOptionId = answerOptionId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
