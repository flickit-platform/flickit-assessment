package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.DeleteAssessmentUserInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentsUserInvitationsPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.core.test.fixture.adapter.jpa.AssessmentInviteeJpaEntityMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.USER_ID_NOT_FOUND;
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
    @DisplayName("The user with the specified userId should have been registered previously.")
    void testAcceptAssessmentInvitations_userIdNotFound_NotFoundError(){
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
    @DisplayName("If input parameter are valid, the service should do granting user invitees, successfully")
    void testAcceptAssessmentInvitations_validParameters_SuccessfullyGrantingUserInvitees(){
        var userId = UUID.randomUUID();
        var email = "test@test.com";
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);
        var assessmentInvitee1 = AssessmentInviteeJpaEntityMother.createAssessmentInvitee(email,1);
        var assessmentInvitee2 = AssessmentInviteeJpaEntityMother.createAssessmentInvitee(email,2);
        var assessmentInviteeList = List.of(assessmentInvitee1, assessmentInvitee2);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadAssessmentsUserInvitationsPort.loadInvitations(email)).thenReturn(assessmentInviteeList);
        doNothing().when(grantUserAssessmentRolePort).persistAll(any());
        doNothing().when(deleteAssessmentUserInvitationPort).deleteAssessmentUserInvitationsByEmail(email);

        assertDoesNotThrow(() -> service.acceptInvitations(param));

        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadAssessmentsUserInvitationsPort).loadInvitations(email);
        verify(grantUserAssessmentRolePort).persistAll(any());
    }
}
