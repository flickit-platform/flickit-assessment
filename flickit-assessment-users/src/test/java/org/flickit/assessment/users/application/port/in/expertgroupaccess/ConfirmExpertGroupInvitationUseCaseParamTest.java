package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfirmExpertGroupInvitationUseCaseParamTest {

    @Test
    void testConfirmExpertGroupInviteParam_expertGroupIdIsNull_ErrorMessage() {
        UUID inviteToken = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ConfirmExpertGroupInvitationUseCase.Param(null, inviteToken, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + CONFIRM_EXPERT_GROUP_INVITATION_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testConfirmExpertGroupInviteParam_currentUserIdIsNull_ErrorMessage() {
        UUID inviteToken = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ConfirmExpertGroupInvitationUseCase.Param(0L, inviteToken, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testConfirmExpertGroupInviteParam_inviteTokenIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ConfirmExpertGroupInvitationUseCase.Param(0L, null, currentUserId));
        assertThat(throwable).hasMessage("inviteToken: " + CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_NOT_NULL);
    }
}
