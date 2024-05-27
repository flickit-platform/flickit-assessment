package org.flickit.assessment.users.application.port.in.spaceinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_SPACE_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_INVITATION_EMAIL_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSpaceInvitationUseCaseParamTest {

    @Test
    void testDeleteSpaceInvitationUseCaseParam_spaceIdIsNull_ErrorMessage() {
        String email = "admin@flickit.ir";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceInvitationUseCase.Param(null, email, currentUserId));
        assertThat(throwable).hasMessage("spaceId: " + DELETE_SPACE_INVITATION_SPACE_ID_NOT_NULL);
    }

    @Test
    void testDeleteSpaceInvitationUseCaseParam_userIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceInvitationUseCase.Param(spaceId, null, currentUserId));
        assertThat(throwable).hasMessage("email: " + DELETE_SPACE_INVITATION_EMAIL_NOT_NULL);
    }

    @Test
    void testDeleteSpaceInvitationUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        String email = "admin@flickit.ir";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteSpaceInvitationUseCase.Param(123L, email, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
