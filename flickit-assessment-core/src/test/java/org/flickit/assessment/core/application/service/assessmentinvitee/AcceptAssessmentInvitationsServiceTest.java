package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.DeleteAssessmentUserInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentsUserInvitationsPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInviteeMother.expiredAssessmentInvitee;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInviteeMother.notExpiredAssessmentInvitee;
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
    @DisplayName("The user with the given userId does not exist, should throw notFoundException.")
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
    @DisplayName("If input parameter are valid, the service should convert invitations to access successfully")
    void testAcceptAssessmentInvitations_validParameters_SuccessfullyAcceptInvitations() {
        var userId = UUID.randomUUID();
        var email = "test@test.com";
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);
        var assessmentInvitee1 = expiredAssessmentInvitee(email);
        var assessmentInvitee2 = notExpiredAssessmentInvitee(email);
        var assessmentInviteeList = List.of(assessmentInvitee1, assessmentInvitee2);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenReturn(email);
        when(loadAssessmentsUserInvitationsPort.loadInvitations(email)).thenReturn(assessmentInviteeList);
        doNothing().when(grantUserAssessmentRolePort).persistAll(any());
        doNothing().when(deleteAssessmentUserInvitationPort).deleteAllByEmail(email);

        assertDoesNotThrow(() -> service.acceptInvitations(param));

        // Capture the argument passed to persistAll
        ArgumentCaptor<List<AssessmentUserRoleItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(grantUserAssessmentRolePort).persistAll(captor.capture());

        List<AssessmentUserRoleItem> capturedList = captor.getValue();
        var assessmentUserRoleItem = new AssessmentUserRoleItem(assessmentInvitee2.getAssessmentId(), userId, assessmentInvitee2.getRole());

        // Assert that the captured list contains exactly one item ,and it is equal to the expected item
        assertEquals(1, capturedList.size());
        assertEquals(assessmentUserRoleItem.getAssessmentId(), capturedList.get(0).getAssessmentId());
        assertEquals(assessmentUserRoleItem.getUserId(), capturedList.get(0).getUserId());
        assertEquals(assessmentUserRoleItem.getRole().getId(), capturedList.get(0).getRole().getId());

        verify(loadUserEmailByUserIdPort).loadEmail(userId);
        verify(loadAssessmentsUserInvitationsPort).loadInvitations(email);
    }
}
