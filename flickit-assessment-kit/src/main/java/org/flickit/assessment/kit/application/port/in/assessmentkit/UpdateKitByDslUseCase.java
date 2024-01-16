package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL;

public interface UpdateKitByDslUseCase {

    void update(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotBlank(message = UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL)
        String dslContent;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, String dslContent, UUID currentUserId) {
            this.kitId = kitId;
            this.dslContent = dslContent;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
