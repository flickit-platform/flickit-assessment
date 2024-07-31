package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class InviteAssessmentUserUseCaseParamTest {

    @Test
    void testInviteAssessmentUserParam_assessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String email = "test@test.com";
        int roleId = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(null, email, roleId, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_emailIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        int roleId = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(assessmentId, null, roleId, currentUserId));
        assertThat(throwable).hasMessage("email: " + INVITE_ASSESSMENT_USER_EMAIL_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_roleIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String email = "test@test.com";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(assessmentId, email, null, currentUserId));
        assertThat(throwable).hasMessage("roleId: " + INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_currentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        String email = "test@test.com";
        int roleId = 1;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(assessmentId, email, roleId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_Email_SuccessfulStripAndIgnoreCase() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        String email1 = "test@test.com";
        String email2 = " Test@test.com    ";
        int roleId = 1;

        var param1 =  new InviteAssessmentUserUseCase.Param(assessmentId, email1, roleId, currentUserId);
        var param2 = new InviteAssessmentUserUseCase.Param(assessmentId, email2, roleId, currentUserId);
        assertEquals(param1.getEmail(), param2.getEmail(), "The input email should be stripped, and the case should be ignored.");
    }
}
