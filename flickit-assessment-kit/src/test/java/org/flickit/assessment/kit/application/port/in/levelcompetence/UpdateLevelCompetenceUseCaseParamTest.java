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
    void testUpdateLevelCompetenceUseCaseParam_LevelCompetenceIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.levelCompetenceId(null)));
        assertThat(throwable.getMessage()).isEqualTo("levelCompetenceId: " + UPDATE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetenceUseCaseParam_KitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable.getMessage()).isEqualTo("kitVersionId: " + UPDATE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetenceUseCaseParam_ValueParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(null)));
        assertThat(throwable.getMessage()).isEqualTo("value: " + UPDATE_LEVEL_COMPETENCE_VALUE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(-1)));
        assertThat(throwable).hasMessage("value: " + UPDATE_LEVEL_COMPETENCE_VALUE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.value(101)));
        assertThat(throwable).hasMessage("value: " + UPDATE_LEVEL_COMPETENCE_VALUE_MAX);
    }

    @Test
    void testUpdateLevelCompetenceUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b->b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetenceUseCaseParam_ValidParams_ShouldNotThrowException() {
        assertDoesNotThrow(() -> createParam(UpdateLevelCompetenceUseCase.Param.ParamBuilder::build));
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
