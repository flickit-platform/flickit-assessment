package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.application.domain.User;

import java.time.LocalDateTime;

import static org.flickit.assessment.users.common.ErrorMessageKey.*;

public interface GetUserByEmailUseCase {

    Result getUserByEmail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_USER_BY_EMAIL_EMAIL_NOT_NULL)
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = EMAIL_NOT_VALID)
        String email;

        public Param(String email) {
            this.email = email != null ? email.strip().toLowerCase() : null;
            validateSelf();
        }
    }

    record Result(User user,
                  LocalDateTime lastLogin,
                  boolean isSuperUser,
                  boolean isStaff,
                  boolean isActive,
                  String password) {
    }
}
