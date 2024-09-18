package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface CreateAssessorAdviceNarrationUseCase {

    void createAssessorAdviceNarration(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @Size(max = 1000, message = CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX)
        String assessorNarration;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, String assessorNarration, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.assessorNarration = assessorNarration;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
