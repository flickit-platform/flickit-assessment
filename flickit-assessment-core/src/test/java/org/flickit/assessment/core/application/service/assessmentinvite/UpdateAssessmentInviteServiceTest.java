package org.flickit.assessment.core.application.service.assessmentinvite;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessmentinvite.UpdateAssessmentInviteUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.UpdateAssessmentInvitePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_INVITE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITE_ROLE_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother.notExpiredAssessmentInvite;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentInviteServiceTest {

    @InjectMocks
    UpdateAssessmentInviteService service;

    @Mock
    LoadAssessmentInvitePort loadAssessmentInvitationPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    UpdateAssessmentInvitePort updateAssessmentInvitePort;

    @Test
    void testUpdateAssessmentInviteService_invitationNotFound_ThrowNotFoundException() {
        var inviteId = UUID.randomUUID();
        var param = new Param(inviteId, 1, UUID.randomUUID());

        when(loadAssessmentInvitationPort.load(inviteId)).thenThrow(new ResourceNotFoundException(ASSESSMENT_INVITE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateInvite(param));
        assertEquals(ASSESSMENT_INVITE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadAssessmentInvitationPort).load(param.getInviteId());
        verifyNoInteractions(assessmentAccessChecker, updateAssessmentInvitePort);
    }

    @Test
    void testUpdateAssessmentInviteService_currentUserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        var inviteId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(inviteId, 1, currentUserId);
        var assessmentInvitee = notExpiredAssessmentInvite("test@flickit.org");

        when(loadAssessmentInvitationPort.load(inviteId)).thenReturn(assessmentInvitee);
        when(assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateInvite(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadAssessmentInvitationPort).load(param.getInviteId());
        verify(assessmentAccessChecker).isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE);
        verifyNoInteractions(updateAssessmentInvitePort);
    }

    @Test
    void testUpdateAssessmentInviteService_validParameters_SuccessUpdate() {
        var inviteId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var assessmentInvitee = notExpiredAssessmentInvite("test@flickit.org");
        var roleId = assessmentInvitee.getRole().getId() + 1;
        var param = new Param(inviteId, roleId, currentUserId);

        when(loadAssessmentInvitationPort.load(inviteId)).thenReturn(assessmentInvitee);
        when(assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        doNothing().when(updateAssessmentInvitePort).updateRole(inviteId, roleId);

        assertDoesNotThrow(() -> service.updateInvite(param), "Should update successfully without any exceptions");

        verify(loadAssessmentInvitationPort).load(param.getInviteId());
        verify(assessmentAccessChecker).isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE);
        verify(updateAssessmentInvitePort).updateRole(param.getInviteId(), param.getRoleId());
    }
}
