package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateQuestionUseCase {

    long createQuestion(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_QUESTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_QUESTION_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = CREATE_QUESTION_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_QUESTION_TITLE_SIZE_MIN)
        @Size(max = 250, message = CREATE_QUESTION_TITLE_SIZE_MAX)
        String title;

        @Size(min = 3, message = CREATE_QUESTION_HINT_SIZE_MIN)
        @Size(max = 1000, message = CREATE_QUESTION_HINT_SIZE_MAX)
        String hint;

        @NotNull(message = CREATE_QUESTION_MAY_NOT_BE_APPLICABLE_NOT_NULL)
        Boolean mayNotBeApplicable;

        @NotNull(message = CREATE_QUESTION_ADVISABLE_NOT_NULL)
        Boolean advisable;

        @NotNull(message = CREATE_QUESTION_QUESTIONNAIRE_ID_NOT_NULL)
        Long questionnaireId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId,
                     Integer index,
                     String title,
                     String hint,
                     Boolean mayNotBeApplicable,
                     Boolean advisable,
                     Long questionnaireId,
                     UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title : null;
            this.hint = hint != null && !hint.isBlank() ? hint : null;
            this.mayNotBeApplicable = mayNotBeApplicable;
            this.advisable = advisable;
            this.questionnaireId = questionnaireId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
