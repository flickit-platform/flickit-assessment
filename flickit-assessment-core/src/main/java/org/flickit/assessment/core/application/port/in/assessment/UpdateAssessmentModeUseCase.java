package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.validation.EnumValue;
import org.flickit.assessment.core.application.domain.AssessmentMode;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface UpdateAssessmentModeUseCase {

    void updateAssessmentMode(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_MODE_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = UPDATE_ASSESSMENT_MODE_MODE_NOT_NULL)
        @EnumValue(enumClass = AssessmentMode.class, message = UPDATE_ASSESSMENT_MODE_MODE_INVALID)
        String mode;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, String mode, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.mode = mode;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
