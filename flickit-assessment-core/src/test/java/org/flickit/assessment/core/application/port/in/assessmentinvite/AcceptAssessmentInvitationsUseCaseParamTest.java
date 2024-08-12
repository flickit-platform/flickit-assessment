package org.flickit.assessment.core.application.port.in.assessmentinvite;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.flickit.assessment.core.common.ErrorMessageKey.ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_NULL;

class AcceptAssessmentInvitationsUseCaseParamTest {

    @Test
    void testAcceptAssessmentInvitations_userIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AcceptAssessmentInvitationsUseCase.Param(null));
        assertThat(throwable).hasMessage("userId: " + ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_NULL);
    }
}
