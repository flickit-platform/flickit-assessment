package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.GET_USER_BY_EMAIL_EMAIL_NOT_NULL;

public interface GetUserIdByEmailUseCase {

    UUID getUserIdByEmail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = GET_USER_BY_EMAIL_EMAIL_NOT_NULL)
        String email;

        public Param(String email) {
            this.email = email;
            validateSelf();
        }
    }
}
