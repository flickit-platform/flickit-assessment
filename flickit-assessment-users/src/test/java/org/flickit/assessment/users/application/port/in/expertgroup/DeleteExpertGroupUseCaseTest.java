package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteExpertGroupUseCaseTest {

    private static final Long ID = 1L;

    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testDeleteExpertGroupParam_idIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupUseCase.Param(null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("id: " + DELETE_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testDeleteExpertGroupParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteExpertGroupUseCase.Param(ID, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
