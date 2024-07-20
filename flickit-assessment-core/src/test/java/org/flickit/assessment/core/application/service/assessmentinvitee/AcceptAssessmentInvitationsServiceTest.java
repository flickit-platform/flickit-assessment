package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptAssessmentInvitationsServiceTest {

    @InjectMocks
    private AcceptAssessmentInvitationsService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Test
    @DisplayName("The user with the specified userId should be registered previously.")
    void testAcceptAssessmentInvitations_userIdNotFound_NotFoundError(){
        var userId = UUID.randomUUID();
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);

        when(loadUserPort.loadById(userId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.acceptInvitations(param));

        assertEquals(ACCEPT_ASSESSMENT_INVITATIONS_USER_ID_NOT_FOUND, throwable.getMessage());
        verify(loadUserPort).loadById(userId);
    }
}
