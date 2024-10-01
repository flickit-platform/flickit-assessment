package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateLevelCompetenceUseCaseParamTest {

    @Test
    void testUpdateLevelCompetence_LevelCompetenceIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.levelCompetenceId(null)));
        assertThat(throwable.getMessage()).isEqualTo("levelCompetenceId: " + UPDATE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_KitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable.getMessage()).isEqualTo("kitVersionId: " + UPDATE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_ValueParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable.getMessage()).isEqualTo("value: " + UPDATE_LEVEL_COMPETENCE_VALUE_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_ValidParams_ShouldNotThrowException() {
        var currentUserId = UUID.randomUUID();
        assertDoesNotThrow(() -> new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId));
    }

    private void createParam(Consumer<UpdateLevelCompetenceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateLevelCompetenceUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateLevelCompetenceUseCase.Param.builder()
            .levelCompetenceId(123L)
            .kitVersionId(1L)
            .value(55)
            .currentUserId(UUID.randomUUID());
    }
}
