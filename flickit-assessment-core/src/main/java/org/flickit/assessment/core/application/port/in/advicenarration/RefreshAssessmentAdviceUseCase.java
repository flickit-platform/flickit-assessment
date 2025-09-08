package org.flickit.assessment.core.application.port.in.advicenarration;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REFRESH_ASSESSMENT_ADVICE_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REFRESH_ASSESSMENT_ADVICE_FORCE_REGENERATE_NOT_NULL;

public interface RefreshAssessmentAdviceUseCase {

    void refreshAssessmentAdvice(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = REFRESH_ASSESSMENT_ADVICE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = REFRESH_ASSESSMENT_ADVICE_FORCE_REGENERATE_NOT_NULL)
        Boolean forceRegenerate;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Boolean forceRegenerate, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.forceRegenerate = forceRegenerate;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
