package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetAssessmentProgressUseCaseParamTest {

    @Test
    void testGetAssessmentProgress_AssessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAssessmentProgressUseCase.Param(null));
        Assertions.assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_PROGRESS_ASSESSMENT_ID_NOT_NULL);
    }
}
