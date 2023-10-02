package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CountAssessmentsUseCaseParamTest {

    @Test
    void testCountAssessments_AssessmentKitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CountAssessmentsUseCase.Param(null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE));
        assertThat(throwable).hasMessage("assessmentKitId: " + COUNT_ASSESSMENTS_ASSESSMENT_KIT_ID_NOT_NULL);
    }

}
