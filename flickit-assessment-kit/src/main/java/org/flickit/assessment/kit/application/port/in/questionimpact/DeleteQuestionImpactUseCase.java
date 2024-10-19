package org.flickit.assessment.kit.application.port.in.questionimpact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_IMPACT_QUESTION_IMPACT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface DeleteQuestionImpactUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param>{

        @NotNull(message = DELETE_QUESTION_IMPACT_QUESTION_IMPACT_ID_NOT_NULL)
        Long questionImpactId;

        @NotNull(message = DELETE_QUESTION_IMPACT_KIT_VERSION_ID_NOT_NULL )
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long questionImpactId, Long kitVersionId, UUID currentUserId) {
            this.questionImpactId = questionImpactId;
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
