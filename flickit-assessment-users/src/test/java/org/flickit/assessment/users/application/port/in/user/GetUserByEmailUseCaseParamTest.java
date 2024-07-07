package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_USER_BY_EMAIL_EMAIL_NOT_BLANK;
import static org.junit.jupiter.api.Assertions.*;

class GetUserByEmailUseCaseParamTest {

    @Test
    void testGetUserByEmailParam_emailIsBlank_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserByEmailUseCase.Param("  "));
        assertThat(throwable).hasMessage("email: " + GET_USER_BY_EMAIL_EMAIL_NOT_BLANK);
    }

    @Test
    void testGetUserByEmailParam_emailIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserByEmailUseCase.Param(null));
        assertThat(throwable).hasMessage("email: " + GET_USER_BY_EMAIL_EMAIL_NOT_BLANK);
    }
}
