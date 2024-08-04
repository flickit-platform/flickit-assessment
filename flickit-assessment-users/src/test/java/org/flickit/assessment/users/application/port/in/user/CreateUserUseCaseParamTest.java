package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertThat(throwable).hasMessage("email: " + EMAIL_FORMAT_NOT_VALID);
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
    void testCreateUserParam_EmailIsBlank_ErrorMessage() {
        UUID id = UUID.randomUUID();
        String email = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(id, email, "abc"));
        assertThat(throwable).hasMessage("email: " + CREATE_USER_EMAIL_NOT_NULL);
    }

    @Test
    void testCreateUserParam_EmailIsNull_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateUserUseCase.Param(id, null, "abc"));
        assertThat(throwable).hasMessage("email: " + CREATE_USER_EMAIL_NOT_NULL);
    }

    @Test
    void testCreateUserParam_Email_SuccessfulStripAndIgnoreCase() {
        UUID id = UUID.randomUUID();
        String email1 = "test@test.com";
        String email2 = " Test@test.com    ";
        var param1 =  new CreateUserUseCase.Param(id, email1, "abc");
        var param2 = new CreateUserUseCase.Param(id, email2, "def");
        assertEquals(param1.getEmail(), param2.getEmail(), "The input email should be stripped, and the case should be ignored.");
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
