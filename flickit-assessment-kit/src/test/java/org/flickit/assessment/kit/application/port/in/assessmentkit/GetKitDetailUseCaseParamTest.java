package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DETAIL_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitDetailUseCaseParamTest {

    @Test
    void testGetKitDetail_kitVersionIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitDetailUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_DETAIL_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitDetail_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitDetailUseCase.Param(25L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
