package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.common.ErrorMessageKey;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_SPACE_SPACE_ID_NOT_NULL;

public interface UpdateSpaceUseCase {

    void updateSpace(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_SPACE_SPACE_ID_NOT_NULL)
        Long id;

        @NotBlank(message = ErrorMessageKey.UPDATE_SPACE_TITLE_NOT_BLANK)
        @Size(min = 3, message = ErrorMessageKey.UPDATE_SPACE_TITLE_SIZE_MIN)
        @Size(max = 100, message = ErrorMessageKey.UPDATE_SPACE_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long id, String title, UUID currentUserId) {
            this.id = id;
            this.title = title != null ? title.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Long id) {
    }
}
