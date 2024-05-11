package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.application.domain.User;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_BIO_SIZE_MAX;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_DISPLAY_NAME_NOT_BLANK;
import static org.flickit.assessment.users.common.ErrorMessageKey.UPDATE_USER_LINKEDIN_NOT_VALID;

public interface UpdateUserUseCase {

    User updateUser(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_USER_ID_NOT_NULL)
        UUID userId;

        @NotBlank(message = UPDATE_USER_DISPLAY_NAME_NOT_BLANK)
        String displayName;

        @Size(max = 400, message = UPDATE_USER_BIO_SIZE_MAX)
        String bio;

        @Pattern(regexp = "(?:https?://)?(?:www\\.)?linkedin\\.com/in/[\\w-]+\n", message = UPDATE_USER_LINKEDIN_NOT_VALID)
        String linkedin;

        public Param(UUID userId, String displayName, String bio, String linkedin) {
            this.userId = userId;
            this.displayName = displayName;
            this.bio = bio;
            this.linkedin = linkedin;
            validateSelf();
        }
    }
}
