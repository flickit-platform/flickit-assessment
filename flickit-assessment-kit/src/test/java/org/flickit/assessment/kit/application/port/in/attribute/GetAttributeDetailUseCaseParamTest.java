package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetAttributeDetailUseCaseParamTest {

    public static final Long KIT_ID = 2L;
    public static final Long ATTRIBUTE_ID = 14L;
    public static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testGetAttributeDetail_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeDetailUseCase.Param(null, ATTRIBUTE_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeDetail_attributeIdsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeDetailUseCase.Param(KIT_ID, null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("attributeId: " + GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeDetail_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeDetailUseCase.Param(KIT_ID, ATTRIBUTE_ID, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
