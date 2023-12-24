package org.flickit.assessment.kit.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_USER_ID_BY_EMAIL_EMAIL_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetUserIdByEmailUseCaseParamTest {

    @Test
    void testGetUserIdByEmailParam_emailIsBlank_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetUserIdByEmailUseCase.Param("  "));
        assertThat(throwable).hasMessage("email: " + GET_USER_ID_BY_EMAIL_EMAIL_NOT_NULL);
    }
}
