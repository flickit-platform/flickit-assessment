package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.SET_KIT_CUSTOM_TO_ASSESSMENT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.SET_KIT_CUSTOM_TO_ASSESSMENT_KIT_CUSTOM_ID_NOT_NULL;

public interface SetKitCustomToAssessmentUseCase {

    void setKitCustomToAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = SET_KIT_CUSTOM_TO_ASSESSMENT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = SET_KIT_CUSTOM_TO_ASSESSMENT_KIT_CUSTOM_ID_NOT_NULL)
        Long kitCustomId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long kitCustomId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.kitCustomId = kitCustomId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
