package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface UpdateUserUseCase {

    void updateUser(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @NotBlank(message = UPDATE_USER_DISPLAY_NAME_NOT_BLANK)
        String displayName;

        @Size(max = 400, message = UPDATE_USER_BIO_SIZE_MAX)
        String bio;

        @Pattern(regexp = "(?:https?://)?(?:www\\.)?linkedin\\.com/in/[\\w-]+", message = UPDATE_USER_LINKEDIN_NOT_VALID)
        String linkedin;

        public Param(UUID currentUserId, String displayName, String bio, String linkedin) {
            this.currentUserId = currentUserId;
            this.displayName = displayName;
            this.bio = bio;
            this.linkedin = linkedin;
            validateSelf();
        }
    }
}
