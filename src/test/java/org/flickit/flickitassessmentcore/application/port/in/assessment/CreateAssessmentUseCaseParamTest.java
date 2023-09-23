package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_COLOR_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentUseCaseParamTest {

    @Test
    void testCreateAssessment_ColorIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(1L, "title example", 1L, null));
        Assertions.assertThat(throwable).hasMessage("colorId: " + CREATE_ASSESSMENT_COLOR_ID_NOT_NULL);
    }
}
