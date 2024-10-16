package org.flickit.assessment.core.application.port.in.assessmentinvite;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITE_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAssessmentInviteUseCaseParamTest {

    @Test
    void testUpdateAssessmentInviteUseCaseParam_inviteIdIsNull_ErrorMessage() {
        int roleId = 1;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentInviteUseCase.Param(null, roleId, currentUserId));
        assertThat(throwable).hasMessage("inviteId: " + UPDATE_ASSESSMENT_INVITE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentInviteUseCaseParam_roleIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentInviteUseCase.Param(inviteId, null, currentUserId));
        assertThat(throwable).hasMessage("roleId: " + UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentInviteUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        int roleId = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateAssessmentInviteUseCase.Param(inviteId, roleId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
