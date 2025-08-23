package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSIGN_KIT_CUSTOM_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSIGN_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL;

public interface AssignKitCustomUseCase {

    void assignKitCustom(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ASSIGN_KIT_CUSTOM_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = ASSIGN_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL)
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
