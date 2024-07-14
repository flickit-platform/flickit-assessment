package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InviteAssessmentUserUseCaseParamTest {

    @Test
    void testInviteAssessmentUserParam_assessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        int roleId = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(null, userId, roleId));
        assertThat(throwable).hasMessage("assessmentId: " + INVITE_ASSESSMENT_USER_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_userIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        int roleId = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(assessmentId, null, roleId));
        assertThat(throwable).hasMessage("userId: " + INVITE_ASSESSMENT_USER_USER_ID_NOT_NULL);
    }

    @Test
    void testInviteAssessmentUserParam_roleIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new InviteAssessmentUserUseCase.Param(assessmentId, userId, null));
        assertThat(throwable).hasMessage("roleId: " + INVITE_ASSESSMENT_USER_ROLE_ID_NOT_NULL);
    }
}
