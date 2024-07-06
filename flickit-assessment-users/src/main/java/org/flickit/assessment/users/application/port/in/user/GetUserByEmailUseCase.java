package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.users.application.domain.User;

import java.time.LocalDateTime;

import static org.flickit.assessment.users.common.ErrorMessageKey.GET_USER_BY_EMAIL_EMAIL_NOT_BLANK;

public interface GetUserByEmailUseCase {

    Result getUserByEmail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = GET_USER_BY_EMAIL_EMAIL_NOT_BLANK)
        String email;

        public Param(String email) {
            this.email = email;
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
