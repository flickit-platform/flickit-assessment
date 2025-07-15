package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSpaceUseCaseParamTest {

    @Test
    void testDeleteSpaceParam_idIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + DELETE_SPACE_SPACE_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteSpaceUseCase.Param.builder()
            .id(0L)
            .currentUserId(UUID.randomUUID());
    }
}
