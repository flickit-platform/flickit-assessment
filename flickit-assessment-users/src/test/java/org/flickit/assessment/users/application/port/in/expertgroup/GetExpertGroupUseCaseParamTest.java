package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetExpertGroupUseCaseParamTest {

    @Test
    void testGetExpertGroupUseCaseParam_idIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("id: " + GET_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testGetExpertGroupUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetExpertGroupUseCase.Param(123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
