package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface UpdateMaturityLevelUseCase {

    void updateMaturityLevel(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_MATURITY_LEVEL_TITLE_NOT_NULL)
        @Size(min = 3, message = UPDATE_MATURITY_LEVEL_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_MATURITY_LEVEL_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = UPDATE_MATURITY_LEVEL_VALUE_NOT_NULL)
        Integer value;

        @NotNull(message = UPDATE_MATURITY_LEVEL_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = UPDATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = UPDATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN)
        String description;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(String title, Integer value, Integer index, String description, UUID currentUserId) {
            this.title = title != null && !title.isBlank() ? title.strip() : null;
            this.value = value;
            this.index = index;
            this.description = description != null && !description.isBlank() ? description.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
