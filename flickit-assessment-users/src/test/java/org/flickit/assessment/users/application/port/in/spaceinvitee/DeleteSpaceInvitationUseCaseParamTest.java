package org.flickit.assessment.users.application.port.in.spaceinvitee;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.users.application.port.in.spaceinvitee.DeleteSpaceInvitationUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_INVITE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSpaceInvitationUseCaseParamTest {

    @Test
    void testDeleteSpaceInvitationUseCaseParam_userIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, currentUserId));
        assertThat(throwable).hasMessage("inviteId: " + DELETE_SPACE_INVITATION_INVITE_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceInvitationUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(inviteId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
