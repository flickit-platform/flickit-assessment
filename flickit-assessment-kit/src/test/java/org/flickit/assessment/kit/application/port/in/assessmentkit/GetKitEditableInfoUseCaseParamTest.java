package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetKitEditableInfoUseCaseParamTest {

    @Test
    void testGetKitEditableInfo_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitEditableInfoUseCase.Param(null));
        assertThat(throwable).hasMessage("assessmentKitId: " + GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL);
    }
}
