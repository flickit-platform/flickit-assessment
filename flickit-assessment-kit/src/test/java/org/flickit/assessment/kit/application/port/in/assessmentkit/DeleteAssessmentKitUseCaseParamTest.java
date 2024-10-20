package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteAssessmentKitUseCaseParamTest {

    @Test
    void testDeleteAssessmentKit_WhenKitIdNull_ThenErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteAssessmentKitUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + DELETE_KIT_KIT_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentKit_WhenCurrentUserIdNull_ThenErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteAssessmentKitUseCase.Param(1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
