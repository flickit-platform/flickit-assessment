package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteLevelCompetenceUseCaseParamTest {

    @Test
    void testDeleteLevelCompetenceUseCaseParam_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + DELETE_LEVEL_COMPETENCE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testDeleteLevelCompetenceUseCaseParam_levelCompetenceIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.levelCompetenceId(null)));
        assertThat(throwable).hasMessage("levelCompetenceId: " + DELETE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL);
    }

    @Test
    void testDeleteLevelCompetenceUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteLevelCompetenceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteLevelCompetenceUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteLevelCompetenceUseCase.Param.builder()
            .kitVersionId(1L)
            .levelCompetenceId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
