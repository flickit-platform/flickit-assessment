package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.UpdateAssessmentInviteeRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.UpdateAssessmentInviteeRolePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInviteeMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_INVITEE_ROLE_INVITE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentInviteeRoleServiceTest {

    @InjectMocks
    UpdateAssessmentInviteeRoleService service;

    @Mock
    LoadAssessmentInvitationPort loadAssessmentInvitationPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    UpdateAssessmentInviteeRolePort updateAssessmentInviteeRolePort;

    @Test
    @DisplayName("Edit Assessment Invitee Role Service - Existing Invitation Not Found - Throws NotFoundException")
    void testEditAssessmentInviteeRoleService_invitationNotFound_NotFoundException() {
        var inviteId = UUID.randomUUID();
        var inviteRoleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(inviteId, inviteRoleId, currentUserId);

        when(loadAssessmentInvitationPort.load(inviteId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.editRole(param));

        assertEquals(UPDATE_ASSESSMENT_INVITEE_ROLE_INVITE_ID_NOT_FOUND, throwable.getMessage(), "The invitation should exist");

        verify(loadAssessmentInvitationPort).load(param.getInviteId());
        verifyNoInteractions(assessmentAccessChecker, updateAssessmentInviteeRolePort);
    }

    @Test
    @DisplayName("Edit Assessment Invitee Role Service - Current User Lacks Access - Throws NotFoundException")
    void testEditAssessmentInviteeRoleService_currentUserDoesNotHaveAccess_NotFoundException() {
        var inviteId = UUID.randomUUID();
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(inviteId, roleId, currentUserId);
        var assessmentInvitee = AssessmentInviteeMother.notExpiredAssessmentInvitee("test@flickit.org");

        when(loadAssessmentInvitationPort.load(inviteId)).thenReturn(Optional.of(assessmentInvitee));
        when(assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.editRole(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage(), "User is not allowed to edit assessment invitee");

        verify(loadAssessmentInvitationPort).load(param.getInviteId());
        verify(assessmentAccessChecker).isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE);
        verifyNoInteractions(updateAssessmentInviteeRolePort);
    }

    @Test
    @DisplayName("Edit Assessment Invitee Role Service - Valid Parameters - Should Update Successfully")
    void testEditAssessmentInviteeRoleService_validParameters_ShouldUpdate() {
        var inviteId = UUID.randomUUID();
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(inviteId, roleId, currentUserId);
        var assessmentInvitee = AssessmentInviteeMother.notExpiredAssessmentInvitee("test@flickit.org");

        when(loadAssessmentInvitationPort.load(inviteId)).thenReturn(Optional.of(assessmentInvitee));
        when(assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE)).thenReturn(true);
        doNothing().when(updateAssessmentInviteeRolePort).updateRole(inviteId, roleId);

        assertDoesNotThrow(() -> service.editRole(param), "Should update successfully without any exceptions");

        verify(loadAssessmentInvitationPort).load(param.getInviteId());
        verify(assessmentAccessChecker).isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, GRANT_USER_ASSESSMENT_ROLE);
        verify(updateAssessmentInviteeRolePort).updateRole(param.getInviteId(), param.getRoleId());
    }
}
