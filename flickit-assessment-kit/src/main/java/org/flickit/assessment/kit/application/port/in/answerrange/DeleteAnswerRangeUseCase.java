package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL;

public interface DeleteAnswerRangeUseCase {

    void deleteAnswerRange(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL)
        Long answerRangeId;

        @NotNull(message = DELETE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long answerRangeId, Long kitVersionId, UUID currentUserId) {
            this.answerRangeId = answerRangeId;
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
