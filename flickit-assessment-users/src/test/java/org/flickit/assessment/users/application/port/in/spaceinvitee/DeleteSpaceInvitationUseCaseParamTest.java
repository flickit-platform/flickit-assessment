package org.flickit.assessment.users.application.port.in.spaceinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_SPACE_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_INVITE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSpaceInvitationUseCaseParamTest {

    @Test
    void testDeleteSpaceInvitationUseCaseParam_spaceIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceInvitationUseCase.Param(null, inviteId, currentUserId));
        assertThat(throwable).hasMessage("spaceId: " + DELETE_SPACE_INVITATION_SPACE_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceInvitationUseCaseParam_userIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceInvitationUseCase.Param(spaceId, null, currentUserId));
        assertThat(throwable).hasMessage("inviteId: " + DELETE_SPACE_INVITATION_INVITE_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceInvitationUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceInvitationUseCase.Param(123L, inviteId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
