package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface MoveAssessmentUseCase {

    void moveAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = MOVE_ASSESSMENT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = MOVE_ASSESSMENT_TARGET_SPACE_ID_NOT_NULL)
        Long targetSpaceId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long targetSpaceId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.targetSpaceId = targetSpaceId;
            this.currentUserId = currentUserId;
            validateSelf();
        }
    }
}
