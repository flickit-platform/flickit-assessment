package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_USER_BY_EMAIL_EMAIL_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetUserByEmailUseCaseParamTest {

    @Test
    void testGetUserByEmailParam_emailIsBlank_ErrorMessage() {
        var email = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserByEmailUseCase.Param(email));
        assertThat(throwable).hasMessage("email: " + GET_USER_BY_EMAIL_EMAIL_NOT_NULL);
    }

    @Test
    void testGetUserByEmailParam_emailIsNotValid_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserByEmailUseCase.Param("admin@flickit"));
        assertThat(throwable).hasMessage("email: " + COMMON_EMAIL_FORMAT_NOT_VALID);
    }

    @Test
    void testGetUserByEmailParam_emailIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserByEmailUseCase.Param(null));
        assertThat(throwable).hasMessage("email: " + GET_USER_BY_EMAIL_EMAIL_NOT_NULL);
    }

    @Test
    void testGetUserByEmailParam_email_SuccessfulStripAndIgnoreCase() {
        String email1 = "test@test.com";
        String email2 = " Test@test.com    ";
        var param1 = new GetUserByEmailUseCase.Param(email1);
        var param2 = new GetUserByEmailUseCase.Param(email2);
        assertEquals(param1.getEmail(), param2.getEmail(), "The input email should be stripped, and the case should be ignored.");
    }
}
