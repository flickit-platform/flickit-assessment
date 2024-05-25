package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentServiceTest {

    @InjectMocks
    private GetAssessmentService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAssessment_validResult() {
        Assessment assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));

        Result result = service.getAssessment(new Param(assessmentId, currentUserId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        assertEquals(assessment.getTitle(), result.assessmentTitle());
        assertEquals(assessment.getSpaceId(), result.spaceId());
        assertEquals(assessment.getAssessmentKit().getId(), result.kitId());
        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
    }

    @Test
    void getAssessment_invalidAssessmentId() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.empty());

        Param param = new Param(assessmentId, currentUserId);
        assertThrows(ResourceNotFoundException.class, () -> service.getAssessment(param));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
    }

    @Test
    void getAssessment_UserHasNotAccess() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(false);

        Param param = new Param(assessmentId, currentUserId);
        assertThrows(AccessDeniedException.class, () -> service.getAssessment(param));

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, never()).getAssessmentById(any());
    }
}
