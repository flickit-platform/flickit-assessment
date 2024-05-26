package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_SPACE_SPACE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaveSpaceUseCaseParamTest {

    @Test
    void testLeaveSpaceParam_idIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, currentUserId));
        assertThat(throwable).hasMessage("id: " + LEAVE_SPACE_SPACE_ID_NOT_NULL);
    }

    @Test
    void testLeaveSpaceParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
