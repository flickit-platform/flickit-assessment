package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_SPACE_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSpaceMemberUseCaseParamTest {

    @Test
    void testDeleteSpaceMember_spaceIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceMemberUseCase.Param(null, userId, currentUserId));
        assertThat(throwable).hasMessage("spaceId: " + DELETE_SPACE_MEMBER_SPACE_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceMember_userIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceMemberUseCase.Param(spaceId, null, currentUserId));
        assertThat(throwable).hasMessage("userId: " + DELETE_SPACE_MEMBER_USER_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceMember_currentUserIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceMemberUseCase.Param(123L, userId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
