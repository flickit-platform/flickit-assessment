package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_USER_DISPLAY_NAME_NOT_BLANK;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_USER_EMAIL_NOT_VALID;

public interface CreateUserUseCase {

    Result createUser(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<GetUserProfileUseCase.Param> {

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID userId;

        @Email(message = CREATE_USER_EMAIL_NOT_VALID)
        String email;

        @NotBlank(message = CREATE_USER_DISPLAY_NAME_NOT_BLANK)
        String displayName;

        public Param(UUID userId, String email, String displayName) {
            this.userId = userId;
            this.email = email;
            this.displayName = displayName;
            validateSelf();
        }
    }

    record Result(UUID userId) {}

}
