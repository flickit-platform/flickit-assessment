package org.flickit.assessment.kit.application.port.in.kitversion;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.ACTIVATE_KIT_VERSION_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivateKitVersionUseCaseParamTest {

    @Test
    void testActivateKitVersionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + ACTIVATE_KIT_VERSION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testActivateKitVersionUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}