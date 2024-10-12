package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_IMPACT_LIST_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_IMPACT_LIST_QUESTION_ID_NOT_NULL;

public interface GetQuestionImpactsUseCase {

    Result getQuestionImpacts(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param>{

        @NotNull(message = GET_QUESTION_IMPACT_LIST_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = GET_QUESTION_IMPACT_LIST_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long questionId, Long kitVersionId, UUID currentUserId) {
            this.questionId = questionId;
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(){
    }
}
