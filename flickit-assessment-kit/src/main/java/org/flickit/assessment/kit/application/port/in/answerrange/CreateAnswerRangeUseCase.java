package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateAnswerRangeUseCase {

    Result createAnswerRange(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @Size(min = 3, message = CREATE_ANSWER_RANGE_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_ANSWER_RANGE_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, String title, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.title = title;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(long id){
    }
}
