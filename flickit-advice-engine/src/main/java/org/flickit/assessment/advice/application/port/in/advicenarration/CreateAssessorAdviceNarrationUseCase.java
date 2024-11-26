package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.ID;
import org.stringtemplate.v4.ST;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface CreateAssessorAdviceNarrationUseCase {

    void createAssessorAdviceNarration(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL)
        ID<String> assessmentId;

        @Size(min = 3, message = CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MIN)
        @Size(max = 1500, message = CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX)
        String assessorNarration;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        ID<String> currentUserId;

        @Builder
        public Param(ID<String> assessmentId, String assessorNarration, ID<String> currentUserId) {
            this.assessmentId = assessmentId;
            this.assessorNarration = assessorNarration != null && !assessorNarration.isBlank() ? assessorNarration.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
