package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface CreateUserUseCase {

    Result createUser(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_USER_USER_ID_NOT_NULL)
        UUID userId;

        @NotNull(message = CREATE_USER_EMAIL_NOT_NULL)
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = CREATE_USER_EMAIL_NOT_VALID)
        String email;

        @NotNull(message = CREATE_USER_DISPLAY_NAME_NOT_NULL)
        @Size(min = 3, message = CREATE_USER_DISPLAY_NAME_SIZE_MIN)
        @Size(max = 50, message = CREATE_USER_DISPLAY_NAME_SIZE_MAX)
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
