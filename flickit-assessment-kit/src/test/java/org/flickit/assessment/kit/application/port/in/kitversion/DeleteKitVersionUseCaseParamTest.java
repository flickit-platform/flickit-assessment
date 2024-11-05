package org.flickit.assessment.kit.application.port.in.kitversion;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_VERSION_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteKitVersionUseCaseParamTest {

    @Test
    void testDeleteKitVersionUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_KIT_VERSION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteKitVersionUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteKitVersionUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private DeleteKitVersionUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteKitVersionUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }

}
