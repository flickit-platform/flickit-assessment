package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPLOAD_KIT_DSL_KIT_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UploadKitUseCaseParamTest {

    @Test
    void testUploadKit_DslFileIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> new UploadKitUseCase.Param(null));
        assertThat(throwable).hasMessage("dslFile: " + UPLOAD_KIT_DSL_KIT_NOT_NULL);
    }
}
