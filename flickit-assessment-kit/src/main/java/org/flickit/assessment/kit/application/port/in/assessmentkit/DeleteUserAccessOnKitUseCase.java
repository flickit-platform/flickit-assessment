package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_USER_ID_NOT_NULL;

public interface DeleteUserAccessOnKitUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<UpdateKitByDslUseCase.Param> {

        @NotNull(message = DELETE_USER_ACCESS_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = DELETE_USER_ACCESS_USER_ID_NOT_NULL)
        UUID userId;

        public Param(Long kitId, UUID userId) {
            this.kitId = kitId;
            this.userId = userId;
            this.validateSelf();
        }
    }
}
