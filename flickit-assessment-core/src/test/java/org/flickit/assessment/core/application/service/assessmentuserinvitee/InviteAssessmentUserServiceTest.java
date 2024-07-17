package org.flickit.assessment.core.application.service.assessmentuserinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase.*;
import org.flickit.assessment.core.application.port.mail.SendFlickitInviteMailPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.InviteSpaceMemberPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.INVITE_ASSESSMENT_USER_EMAIL_DUPLICATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteAssessmentUserServiceTest {

    @InjectMocks
    private InviteAssessmentUserService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Mock
    private InviteSpaceMemberPort inviteSpaceMemberPort;

    @Mock
    private InviteAssessmentUserPort inviteAssessmentUserPort;

    @Mock
    private SendFlickitInviteMailPort sendFlickitInviteMailPort;

    @Test
    @DisplayName("If the assessment is not exists, the service should throw a not found exception.")
    void testInviteAssessmentUser_AssessmentIsNotExist_ResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenThrow(new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.inviteUser(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verifyNoInteractions(loadUserRoleForAssessmentPort, loadUserPort, inviteSpaceMemberPort,
            inviteAssessmentUserPort, sendFlickitInviteMailPort);
    }

    @Test
    @DisplayName("If the user with this email address has already access to flickit, the service should throw a already exist error.")
    void testInviteAssessmentUser_EmailIsExist_AlreadyExistError() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(AssessmentMother.assessment()));
        when(loadUserPort.loadByEmail(email)).thenReturn(new User(UUID.randomUUID(), "name"));

        var throwable = assertThrows(ResourceAlreadyExistsException.class, () -> service.inviteUser(param));
        assertEquals(INVITE_ASSESSMENT_USER_EMAIL_DUPLICATE, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verifyNoInteractions(loadUserRoleForAssessmentPort, inviteAssessmentUserPort, inviteAssessmentUserPort, sendFlickitInviteMailPort);
    }

    @Test
    @DisplayName("If the inviter is not a manager on this assessment, the invitation will fail.")
    void testInviteAssessmentUser_InviterIsNotManager_AccessDenied() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(AssessmentMother.assessment()));
        when(loadUserPort.loadByEmail(email)).thenReturn(null);
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(AssessmentUserRole.VIEWER);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.inviteUser(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(loadUserRoleForAssessmentPort).load(param.getAssessmentId(), param.getCurrentUserId());
    }

    @Test
    @DisplayName("If input parameters are valid, the service should save the invitee record")
    void testInviteAssessmentUser_InviteeRecordIsValid_SuccessfulPersist() {
        var assessmentId = UUID.randomUUID();
        var email = "test@test.com";
        var roleId = 1;
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, email, roleId, currentUserId);
        var assessment = AssessmentMother.assessment();

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadByEmail(email)).thenReturn(null);
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())).thenReturn(AssessmentUserRole.MANAGER);
        doNothing().when(inviteSpaceMemberPort).invite(isA(InviteSpaceMemberPort.Param.class));
        doNothing().when(inviteAssessmentUserPort).invite(isA(InviteAssessmentUserPort.Param.class));

        assertDoesNotThrow(() -> service.inviteUser(param));
        verify(getAssessmentPort).getAssessmentById(assessmentId);
        verify(loadUserPort).loadByEmail(email);
        verify(loadUserRoleForAssessmentPort).load(param.getAssessmentId(), param.getCurrentUserId());
        verify(inviteSpaceMemberPort).invite(any(InviteSpaceMemberPort.Param.class));
        verify(inviteAssessmentUserPort).invite(any(InviteAssessmentUserPort.Param.class));
        verify(sendFlickitInviteMailPort).inviteToFlickit(email);
    }
}
