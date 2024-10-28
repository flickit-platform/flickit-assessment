package org.flickit.assessment.kit.application.port.in.answeroption;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_OPTIONS_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_OPTIONS_QUESTION_ID_NOT_NULL;

public interface GetQuestionOptionsUseCase {

    Result getQuestionOptions(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTION_OPTIONS_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = GET_QUESTION_OPTIONS_QUESTION_ID_NOT_NULL)
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

    record Result(List<Option> answerOptions) {

        public record Option(long id, String title, int index) {}
    }
}
