package org.flickit.assessment.users.application.port.in.spaceuseraccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.ACCEPT_SPACE_INVITATIONS_EMAIL_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptSpaceInvitationsParamTest {

    @Test
    void testAcceptSpaceInvitations_userIdIsNull_ErrorMessage() {
        String email = "admin@filickit.com";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AcceptSpaceInvitationsUseCase.Param(null, email));
        assertThat(throwable).hasMessage("userId: " + ACCEPT_SPACE_INVITATIONS_USER_ID_NOT_NULL);
    }

    @Test
    void testAcceptSpaceInvitations_emailIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AcceptSpaceInvitationsUseCase.Param(userId, null));
        assertThat(throwable).hasMessage("email: " + ACCEPT_SPACE_INVITATIONS_EMAIL_NOT_NULL);
    }
}
