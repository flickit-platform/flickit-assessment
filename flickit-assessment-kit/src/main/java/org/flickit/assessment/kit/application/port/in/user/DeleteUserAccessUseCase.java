package org.flickit.assessment.kit.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.common.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_USER_ACCESS_USER_ID_NOT_NULL;

public interface DeleteUserAccessUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<UpdateKitByDslUseCase.Param> {

        @NotNull(message = DELETE_USER_ACCESS_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = DELETE_USER_ACCESS_USER_ID_NOT_NULL)
        Long userId;

        public Param(Long kitId, Long userId) {
            this.kitId = kitId;
            this.userId = userId;
            this.validateSelf();
        }
    }
}
