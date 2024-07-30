package org.flickit.assessment.core.application.port.in.assessmentinvitee;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_ASSESSMENT_INVITEE_ROLE_INVITE_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_ASSESSMENT_INVITEE_ROLE_ROLE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class EditAssessmentInviteeRoleUseCaseParamTest {

    @Test
    void testEditAssessmentInviteeRoleUseCaseParam_inviteIdIsNull_ErrorMessage() {
        int roleId = 1;
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new EditAssessmentInviteeRoleUseCase.Param(null, roleId, currentUserId));
        assertThat(throwable).hasMessage("inviteId: " + EDIT_ASSESSMENT_INVITEE_ROLE_INVITE_ID_NOT_NULL);
    }

    @Test
    void testEditAssessmentInviteeRoleUseCaseParam_roleIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new EditAssessmentInviteeRoleUseCase.Param(inviteId, null, currentUserId));
        assertThat(throwable).hasMessage("roleId: " + EDIT_ASSESSMENT_INVITEE_ROLE_ROLE_ID_NOT_NULL);
    }

    @Test
    void testEditAssessmentInviteeRoleUseCaseParam_currentUserIdIsNull_ErrorMessage() {
        UUID inviteId = UUID.randomUUID();
        int roleId = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new EditAssessmentInviteeRoleUseCase.Param(inviteId, roleId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
