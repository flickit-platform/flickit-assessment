package org.flickit.assessment.core.application.service.assessmentinvite;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.port.in.assessmentinvite.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.DeleteAssessmentUserInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentsUserInvitationsPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.core.application.service.assessmentinvite.notification.AcceptAssessmentInvitationNotificationCmd;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother.expiredAssessmentInvite;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother.notExpiredAssessmentInvite;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptAssessmentInvitationsServiceTest {

    @InjectMocks
    private AcceptAssessmentInvitationsService service;

    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;

    @Mock
    private LoadAssessmentsUserInvitationsPort loadAssessmentsUserInvitationsPort;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private DeleteAssessmentUserInvitationPort deleteAssessmentUserInvitationPort;

    @Test
    void testAcceptAssessmentInvitations_userIdNotFound_ThrowResourceNotFoundException(){
        var userId = UUID.randomUUID();
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.acceptInvitations(param));

        assertEquals(USER_ID_NOT_FOUND, throwable.getMessage());
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verifyNoInteractions(loadAssessmentsUserInvitationsPort,
            grantUserAssessmentRolePort, deleteAssessmentUserInvitationPort);
    }

    @Test
    void testAcceptAssessmentInvitations_validParameters_SuccessfullyAcceptInvitations() {
        var userId = UUID.randomUUID();
        var email = "test@test.com";
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);
        var assessmentInvitee1 = expiredAssessmentInvite(email);
        var assessmentInvitee2 = notExpiredAssessmentInvite(email);
        var assessmentInviteeList = List.of(assessmentInvitee1, assessmentInvitee2);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadAssessmentsUserInvitationsPort.loadInvitations(email)).thenReturn(assessmentInviteeList);
        doNothing().when(grantUserAssessmentRolePort).persistAll(any());
        doNothing().when(deleteAssessmentUserInvitationPort).deleteAllByEmail(email);

        var result = assertDoesNotThrow(() -> service.acceptInvitations(param));

        ArgumentCaptor<List<AssessmentUserRoleItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(grantUserAssessmentRolePort).persistAll(captor.capture());

        AcceptAssessmentInvitationNotificationCmd cmd = (AcceptAssessmentInvitationNotificationCmd) result.notificationCmd();
        List<AssessmentUserRoleItem> capturedList = captor.getValue();
        var assessmentUserRoleItem = new AssessmentUserRoleItem(assessmentInvitee2.getAssessmentId(), userId, assessmentInvitee2.getRole(), assessmentInvitee2.getCreatedBy());

        assertEquals(1, capturedList.size());
        assertEquals(assessmentUserRoleItem.getAssessmentId(), capturedList.get(0).getAssessmentId());
        assertEquals(assessmentUserRoleItem.getUserId(), capturedList.get(0).getUserId());
        assertEquals(assessmentUserRoleItem.getRole().getId(), capturedList.get(0).getRole().getId());
        assertEquals(1, cmd.notificationCmdItems().size());

        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadAssessmentsUserInvitationsPort).loadInvitations(email);
    }

    @Test
    void testAcceptAssessmentInvitations_validParametersForMoreThanOneAssessment_SuccessfullyAcceptInvitations() {
        var userId = UUID.randomUUID();
        var email = "test@test.com";
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);
        var assessmentInvitee1 = expiredAssessmentInvite(email);
        var assessmentInvitee2 = notExpiredAssessmentInvite(email);
        var assessmentInvitee3 = notExpiredAssessmentInvite(email);
        var assessmentInviteeList = List.of(assessmentInvitee1, assessmentInvitee2, assessmentInvitee3);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadAssessmentsUserInvitationsPort.loadInvitations(email)).thenReturn(assessmentInviteeList);
        doNothing().when(grantUserAssessmentRolePort).persistAll(any());
        doNothing().when(deleteAssessmentUserInvitationPort).deleteAllByEmail(email);

        var result = assertDoesNotThrow(() -> service.acceptInvitations(param));

        AcceptAssessmentInvitationNotificationCmd cmd = (AcceptAssessmentInvitationNotificationCmd) result.notificationCmd();
        ArgumentCaptor<List<AssessmentUserRoleItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(grantUserAssessmentRolePort).persistAll(captor.capture());

        List<AssessmentUserRoleItem> capturedList = captor.getValue();
        var assessmentUserRoleItem1 = new AssessmentUserRoleItem(assessmentInvitee2.getAssessmentId(), userId, assessmentInvitee2.getRole(), assessmentInvitee2.getCreatedBy());
        var assessmentUserRoleItem2 = new AssessmentUserRoleItem(assessmentInvitee3.getAssessmentId(), userId, assessmentInvitee3.getRole(), assessmentInvitee3.getCreatedBy());
        var assessmentUserRoleListItem = List.of(assessmentUserRoleItem1, assessmentUserRoleItem2);

        // Assert that the captured list contains exactly one item ,and it is equal to the expected item
        assertEquals(2, capturedList.size());
        assertEquals(assessmentUserRoleListItem.get(0).getAssessmentId(), capturedList.get(0).getAssessmentId());
        assertEquals(assessmentUserRoleListItem.get(0).getUserId(), capturedList.get(0).getUserId());
        assertEquals(assessmentUserRoleListItem.get(0).getRole().getId(), capturedList.get(0).getRole().getId());
        assertEquals(assessmentUserRoleListItem.get(1).getAssessmentId(), capturedList.get(1).getAssessmentId());
        assertEquals(assessmentUserRoleListItem.get(1).getUserId(), capturedList.get(1).getUserId());
        assertEquals(assessmentUserRoleListItem.get(1).getRole().getId(), capturedList.get(1).getRole().getId());
        assertEquals(2, cmd.notificationCmdItems().size());

        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadAssessmentsUserInvitationsPort).loadInvitations(email);
    }
}
