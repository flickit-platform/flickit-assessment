package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_MATURITY_LEVEL_KIT_ID_NOT_NULL;

public interface DeleteMaturityLevelUseCase {

    void delete(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = DELETE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = DELETE_MATURITY_LEVEL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long maturityLevelId, Long kitId, UUID currentUserId) {
            this.maturityLevelId = maturityLevelId;
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
