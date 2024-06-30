package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculateAssessmentUseCaseParamTest {

    @Test
    void testCalculateAssessmentParam_IdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CalculateAssessmentUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CALCULATE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCalculateAssessmentParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CalculateAssessmentUseCase.Param(assessmentId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
