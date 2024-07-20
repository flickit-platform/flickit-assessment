package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.AcceptAssessmentInvitationsUseCase;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptAssessmentInvitationsServiceTest {

    @InjectMocks
    private AcceptAssessmentInvitationsService service;

    @Mock
    private LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;

    @Test
    @DisplayName("The user with the specified userId should be registered previously.")
    void testAcceptAssessmentInvitations_userIdNotFound_NotFoundError(){
        var userId = UUID.randomUUID();
        var param = new AcceptAssessmentInvitationsUseCase.Param(userId);

        when(loadUserEmailByUserIdPort.loadEmail(userId)).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.acceptInvitations(param));

        assertEquals(USER_ID_NOT_FOUND, throwable.getMessage());
        verify(loadUserEmailByUserIdPort).loadEmail(userId);
    }
}
