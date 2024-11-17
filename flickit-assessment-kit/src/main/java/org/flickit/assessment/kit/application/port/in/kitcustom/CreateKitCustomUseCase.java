package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.KitCustomData;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateKitCustomUseCase {

    long createKitCustom(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_KIT_CUSTOM_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = CREATE_KIT_CUSTOM_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_KIT_CUSTOM_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_KIT_CUSTOM_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_KIT_CUSTOM_DATA_NOT_NULL)
        KitCustomData customData;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, String title, KitCustomData customData, UUID currentUserId) {
            this.kitId = kitId;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.customData = customData;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
