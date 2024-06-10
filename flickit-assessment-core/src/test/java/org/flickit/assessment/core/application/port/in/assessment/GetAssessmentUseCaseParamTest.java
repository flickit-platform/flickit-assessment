package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetAssessmentUseCaseParamTest {

    @Test
    void testGetAssessment_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentUseCase.Param(null, currentUserId));
        Assertions.assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessment_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentUseCase.Param(assessmentId, null));
        Assertions.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
