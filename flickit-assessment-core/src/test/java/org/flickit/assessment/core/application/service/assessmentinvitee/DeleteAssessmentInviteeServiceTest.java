package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.DeleteAssessmentInviteeUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.DeleteAssessmentInviteePort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInviteePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInviteeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT_INVITEE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAssessmentInviteeServiceTest {

    @InjectMocks
    private DeleteAssessmentInviteeService service;

    @Mock
    private DeleteAssessmentInviteePort deleteAssessmentInviteePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentInviteePort loadAssessmentInviteePort;


    @Test
    void testDeleteAssessmentInvitee_WhenDoesNotHaveRequiredPermission_ThenThrowException() {
        AssessmentInvitee assessmentInvitee = AssessmentInviteeMother.notExpiredAssessmentInvitee("user@mail.com");
        UUID id = assessmentInvitee.getId();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteAssessmentInviteeUseCase.Param(id, currentUserId);

        when(loadAssessmentInviteePort.loadById(id)).thenReturn(assessmentInvitee);
        when(assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, DELETE_ASSESSMENT_INVITEE)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.deleteInvitees(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verifyNoInteractions(deleteAssessmentInviteePort);
    }

    @Test
    void testDeleteAssessmentInvitee_WhenValidInput_ThenDelete() {
        AssessmentInvitee assessmentInvitee = AssessmentInviteeMother.notExpiredAssessmentInvitee("user@mail.com");
        UUID id = assessmentInvitee.getId();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteAssessmentInviteeUseCase.Param(id, currentUserId);

        when(loadAssessmentInviteePort.loadById(id)).thenReturn(assessmentInvitee);
        when(assessmentAccessChecker.isAuthorized(assessmentInvitee.getAssessmentId(), currentUserId, DELETE_ASSESSMENT_INVITEE)).thenReturn(true);
        doNothing().when(deleteAssessmentInviteePort).deleteById(id);

        service.deleteInvitees(param);

        verify(deleteAssessmentInviteePort, times(1)).deleteById(id);
    }
}
