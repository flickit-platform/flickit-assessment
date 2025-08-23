package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CLONE_KIT_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class CloneKitUseCaseParamTest {

    @Test
    void testCloneKitUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + CLONE_KIT_KIT_ID_NOT_NULL);
    }

    @Test
    void testCloneKitUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CloneKitUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private CloneKitUseCase.Param.ParamBuilder paramBuilder() {
        return CloneKitUseCase.Param.builder()
                .kitId(1L)
                .currentUserId(UUID.randomUUID());
    }
}
