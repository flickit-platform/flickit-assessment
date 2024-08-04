package org.flickit.assessment.core.application.service.assessmentinvite;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.port.in.assessmentinvite.DeleteAssessmentInviteUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.DeleteAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInvitePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT_INVITE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAssessmentInviteServiceTest {

    @InjectMocks
    private DeleteAssessmentInviteService service;

    @Mock
    private DeleteAssessmentInvitePort deleteAssessmentInvitePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentInvitePort loadAssessmentInvitePort;


    @Test
    void testDeleteAssessmentInvite_WhenDoesNotHaveRequiredPermission_ThenThrowException() {
        AssessmentInvite assessmentInvite = AssessmentInviteMother.notExpiredAssessmentInvite("user@mail.com");
        UUID id = assessmentInvite.getId();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteAssessmentInviteUseCase.Param(id, currentUserId);

        when(loadAssessmentInvitePort.loadById(id)).thenReturn(assessmentInvite);
        when(assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), currentUserId, DELETE_ASSESSMENT_INVITE)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.deleteInvite(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verifyNoInteractions(deleteAssessmentInvitePort);
    }

    @Test
    void testDeleteAssessmentInvite_WhenValidInput_ThenDelete() {
        AssessmentInvite assessmentInvite = AssessmentInviteMother.notExpiredAssessmentInvite("user@mail.com");
        UUID id = assessmentInvite.getId();
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteAssessmentInviteUseCase.Param(id, currentUserId);

        when(loadAssessmentInvitePort.loadById(id)).thenReturn(assessmentInvite);
        when(assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), currentUserId, DELETE_ASSESSMENT_INVITE)).thenReturn(true);
        doNothing().when(deleteAssessmentInvitePort).deleteById(id);

        service.deleteInvite(param);

        verify(deleteAssessmentInvitePort, times(1)).deleteById(id);
    }
}
