package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptSpaceInvitationsUseCaseParamTest {

    @Test
    void testAcceptSpaceInvitations_userIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AcceptSpaceInvitationsUseCase.Param(null));
        assertThat(throwable).hasMessage("userId: " + ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL);
    }
}
