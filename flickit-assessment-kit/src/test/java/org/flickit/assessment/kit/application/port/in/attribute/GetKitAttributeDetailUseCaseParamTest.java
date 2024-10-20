package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitAttributeDetailUseCaseParamTest {

    @Test
    void testGetKitAttributeDetail_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeDetailUseCase.Param(null, 123L, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitAttributeDetail_attributeIdsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeDetailUseCase.Param(123L, null, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetKitAttributeDetail_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeDetailUseCase.Param(123L, 123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
