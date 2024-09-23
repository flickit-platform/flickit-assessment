package org.flickit.assessment.kit.application.port.in.levelcompetence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateLevelCompetenceUseCaseParamTest {

    @Test
    void testUpdateLevelCompetence_IdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateLevelCompetenceUseCase.Param(null, 1L, 2, currentUserId));
        assertThat(throwable.getMessage()).isEqualTo("id: " + UPDATE_LEVEL_COMPETENCE_LEVEL_COMPETENCE_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_KitIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateLevelCompetenceUseCase.Param(1L, null, 3, currentUserId));
        assertThat(throwable.getMessage()).isEqualTo("kitId: " + UPDATE_LEVEL_COMPETENCE_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_ValueIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateLevelCompetenceUseCase.Param(1L, 2L, null, currentUserId));
        assertThat(throwable.getMessage()).isEqualTo("value: " + UPDATE_LEVEL_COMPETENCE_VALUE_NOT_NULL);
    }

    @Test
    void testUpdateLevelCompetence_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
