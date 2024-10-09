package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface AddAnswerOptionUseCase {

    Result addAnswerOption(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ADD_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = ADD_ANSWER_OPTION_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = ADD_ANSWER_OPTION_INDEX_NOT_NULL)
        Integer index;

        @NotBlank(message = ADD_ANSWER_OPTION_TITLE_NOT_BLANK)
        @Size(min = 3, message = ADD_ANSWER_OPTION_TITLE_SIZE_MIN)
        @Size(max = 100, message = ADD_ANSWER_OPTION_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long questionId, Integer index, String title, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.questionId = questionId;
            this.index = index;
            this.title = title;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Long id) {
    }
}
