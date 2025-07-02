package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_EMAIL_FORMAT_NOT_VALID;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateUserUseCaseParamTest {

    @Test
    void testCreateUserParam_UserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.userId(null)));
        assertThat(throwable).hasMessage("userId: " + CREATE_USER_USER_ID_NOT_NULL);
    }

    @Test
    void testCreateUserParam_EmailParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email(null)));
        assertThat(throwable).hasMessage("email: " + CREATE_USER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email(" ")));
        assertThat(throwable).hasMessage("email: " + CREATE_USER_EMAIL_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.email("admin@flickit")));
        assertThat(throwable).hasMessage("email: " + COMMON_EMAIL_FORMAT_NOT_VALID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@test.com", " Test@test.com    "})
    void testCreateUserParam_Email_SuccessfulStripAndIgnoreCase(String email) {
        assertDoesNotThrow(() -> createParam(b -> b.email(email)));
    }

    @Test
    void testCreateUserParam_DisplayNameParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.displayName(null)));
        assertThat(throwable).hasMessage("displayName: " + CREATE_USER_DISPLAY_NAME_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.displayName("ab")));
        assertThat(throwable).hasMessage("displayName: " + CREATE_USER_DISPLAY_NAME_SIZE_MIN);

        String displayName = RandomStringUtils.randomAlphanumeric(51);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.displayName(displayName)));
        assertThat(throwable).hasMessage("displayName: " + CREATE_USER_DISPLAY_NAME_SIZE_MAX);
    }

    private void createParam(Consumer<CreateUserUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateUserUseCase.Param.ParamBuilder paramBuilder() {
        return CreateUserUseCase.Param.builder()
            .userId(UUID.randomUUID())
            .email("admin@flickit.com")
            .displayName("Display Name");
    }
}
