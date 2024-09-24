package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateQuestionUseCase {

    void updateQuestion(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_QUESTION_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = UPDATE_QUESTION_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = UPDATE_QUESTION_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = UPDATE_QUESTION_TITLE_NOT_NULL)
        @Size(min = 3, message = UPDATE_QUESTION_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_QUESTION_TITLE_SIZE_MAX)
        String title;

        @Size(min = 3, message = UPDATE_QUESTION_HINT_SIZE_MIN)
        String hint;

        @NotNull(message = UPDATE_QUESTION_MAY_NOT_BE_APPLICABLE_NOT_NULL)
        Boolean mayNotBeApplicable;

        @NotNull(message = UPDATE_QUESTION_ADVISABLE_NOT_NULL)
        Boolean advisable;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId,
                     Long questionId,
                     Integer index,
                     String title,
                     String hint,
                     Boolean mayNotBeApplicable,
                     Boolean advisable,
                     UUID currentUserId) {
            this.kitId = kitId;
            this.questionId = questionId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title.trim(): null;
            this.hint = hint != null && !hint.isBlank() ? hint.trim(): null;
            this.mayNotBeApplicable = mayNotBeApplicable;
            this.advisable = advisable;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
