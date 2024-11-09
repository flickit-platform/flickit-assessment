package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_RANGE_TITLE_SIZE_MAX;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_RANGE_TITLE_SIZE_MIN;

public interface CreateAnswerRangeUseCase {

    long createAnswerRange(Param param);

    class Param extends SelfValidating<Param> {

        @Size(min = 3, message = CREATE_ANSWER_RANGE_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_ANSWER_RANGE_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(String title, UUID currentUserId) {
            this.title = title;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
