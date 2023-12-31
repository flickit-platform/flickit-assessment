package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_COLOR_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentUseCaseParamTest {

    @Test
    void testCreateAssessment_ColorIsNull_ErrorMessage() {
        UUID createdBy = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(1L, "title example", 1L, null, createdBy));
        Assertions.assertThat(throwable).hasMessage("colorId: " + CREATE_ASSESSMENT_COLOR_ID_NOT_NULL);
    }
}
