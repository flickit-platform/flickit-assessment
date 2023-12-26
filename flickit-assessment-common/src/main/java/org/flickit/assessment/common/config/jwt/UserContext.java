package org.flickit.assessment.common.config.jwt;

import lombok.Setter;
import org.flickit.assessment.common.exception.MissingAuthorizationHeaderException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_HEADER_AUTHORIZATION_NOT_NULL;

@Setter
@Component
@RequestScope
public class UserContext {

    private UserDetail user;

    public UserDetail getUser() {
        if (user != null) {
            return user;
        } else {
            throw new MissingAuthorizationHeaderException(COMMON_HEADER_AUTHORIZATION_NOT_NULL);
        }
    }
}
