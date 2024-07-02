package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateUserUseCaseParamTest {

    @Test
    void testCreateUserParam_UserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(null, "admin@flickit.org", "Flickit Admin"));
        assertThat(throwable).hasMessage("userId: " + CREATE_USER_USER_ID_NOT_NULL);
    }

    @Test
    void testCreateUserParam_NotValidEmail_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(id, "admin@flickit", "Flickit Admin"));
        assertThat(throwable).hasMessage("email: " + CREATE_USER_EMAIL_NOT_VALID);
    }

    @Test
    void testCreateUserParam_NullDisplayName_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(id, "admin@flickit.com", null));
        assertThat(throwable).hasMessage("displayName: " + CREATE_USER_DISPLAY_NAME_NOT_NULL);
    }

    @Test
    void testCreateUserParam_DisplayNameLessThanMinSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(id, "admin@flickit.com", "ab"));
        assertThat(throwable).hasMessage("displayName: " + CREATE_USER_DISPLAY_NAME_SIZE_MIN);
    }

    @Test
    void testCreateUserParam_DisplayNameGreaterThanMaxSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        String displayName = RandomStringUtils.randomAlphanumeric(51);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(id, "admin@flickit.com", displayName));
        assertThat(throwable).hasMessage("displayName: " + CREATE_USER_DISPLAY_NAME_SIZE_MAX);
    }
}
