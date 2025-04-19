package org.flickit.assessment.kit.application.port.in.kitlanguage;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class AddLanguageToKitUseCaseParamTest {

    @Test
    void testAddLanguageToKitUseCaseParam_kitIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + ADD_LANGUAGE_TO_KIT_KIT_ID_NOT_NULL);
    }

    @Test
    void testAddLanguageToKitUseCaseParam_langParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.lang(null)));
        assertThat(throwable).hasMessage("lang: " + ADD_LANGUAGE_TO_KIT_LANG_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.lang("FR")));
        assertThat(throwable).hasMessage("lang: " + ADD_LANGUAGE_TO_KIT_LANGUAGE_INVALID);
    }

    @Test
    void testAddLanguageToKitUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<AddLanguageToKitUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private AddLanguageToKitUseCase.Param.ParamBuilder paramBuilder() {
        return AddLanguageToKitUseCase.Param.builder()
            .kitId(1L)
            .lang("FA")
            .currentUserId(UUID.randomUUID());
    }
}
