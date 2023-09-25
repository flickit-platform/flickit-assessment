package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CountUseCaseParamTest {

    @Test
    void count_NullAssessmentKitId() {
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CountUseCase.Param(null, Boolean.TRUE, Boolean.TRUE));
        assertThat(throwable).hasMessage("assessmentKitId: " + COUNT_ASSESSMENTS_ASSESSMENT_KIT_ID_NOT_NULL);
    }

    @Test
    void count_NullIncludeDeleted() {
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CountUseCase.Param(1L, null, Boolean.TRUE));
        assertThat(throwable).hasMessage("includeDeleted: " + COUNT_ASSESSMENTS_INCLUDE_DELETED_NOT_NULL);
    }

    @Test
    void count_NullIncludeNotDeleted() {
        var throwable = assertThrows(ConstraintViolationException.class, () ->
            new CountUseCase.Param(1L, Boolean.TRUE, null));
        assertThat(throwable).hasMessage("includeNotDeleted: " + COUNT_ASSESSMENTS_INCLUDE_NOT_DELETED_NOT_NULL);
    }

}
