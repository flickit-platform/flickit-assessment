package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.COUNT_ASSESSMENTS_KIT_ID_AND_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CountAssessmentsUseCaseParamTest {

    @Test
    void testCountAssessments_AssessmentKitIdAndSpaceIdAreNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CountAssessmentsUseCase.Param(null, null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE));
        assertThat(throwable).hasMessage("kitIdAndSpaceIdNotNull: " + COUNT_ASSESSMENTS_KIT_ID_AND_SPACE_ID_NOT_NULL);
    }
}
