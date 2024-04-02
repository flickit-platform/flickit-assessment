package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DETAIL_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitDetailUseCaseParamTest {

    @Test
    void testGetKitDetail_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitDetailUseCase.Param(null));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_KIT_DETAIL_KIT_VERSION_ID_NOT_NULL);
    }
}
