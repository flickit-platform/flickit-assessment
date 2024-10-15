package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateMaturityLevelUseCase {

    void updateMaturityLevel(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_MATURITY_LEVEL_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = UPDATE_MATURITY_LEVEL_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_MATURITY_LEVEL_TITLE_NOT_NULL)
        @Size(min = 3, message = UPDATE_MATURITY_LEVEL_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_MATURITY_LEVEL_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = UPDATE_MATURITY_LEVEL_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = UPDATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = UPDATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN)
        @Size(max = 500, message = UPDATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = UPDATE_MATURITY_LEVEL_VALUE_NOT_NULL)
        Integer value;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long maturityLevelId, Long kitVersionId, String title, Integer index, String description, Integer value, UUID currentUserId) {
            this.maturityLevelId = maturityLevelId;
            this.kitVersionId = kitVersionId;
            this.title = title != null && !title.isBlank() ? title.strip() : null;
            this.index = index;
            this.description = description != null && !description.isBlank() ? description.strip() : null;
            this.value = value;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
