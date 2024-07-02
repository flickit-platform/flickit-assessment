package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface UpdateUserProfileUseCase {

    void updateUserProfile(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @NotNull(message = UPDATE_USER_PROFILE_DISPLAY_NAME_NOT_NULL)
        @Size(min = 3, message = UPDATE_USER_PROFILE_DISPLAY_NAME_SIZE_MIN)
        @Size(max = 50, message = UPDATE_USER_PROFILE_DISPLAY_NAME_SIZE_MAX)
        String displayName;

        @Size(min = 3, message = UPDATE_USER_PROFILE_BIO_SIZE_MIN)
        @Size(max = 200, message = UPDATE_USER_PROFILE_BIO_SIZE_MAX)
        String bio;

        @Pattern(regexp = "(?:https?://)?(?:www\\.)?linkedin\\.com(?:/[^\\\\s]*)?", message = UPDATE_USER_PROFILE_LINKEDIN_NOT_VALID)
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
