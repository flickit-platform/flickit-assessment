package org.flickit.assessment.core.application.service.assessment;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentProgressUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_PROGRESS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentProgressServiceTest {

    @InjectMocks
    private GetAssessmentProgressService service;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAssessmentProgress_ValidResult() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_PROGRESS)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(assessmentId))
            .thenReturn(new GetAssessmentProgressPort.Result(assessmentId, 5, 10));

        var result = service.getAssessmentProgress(param);

        ArgumentCaptor<UUID> answerPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentProgressPort).getProgress(answerPortAssessmentId.capture());

        assertEquals(assessmentId, answerPortAssessmentId.getValue());
        verify(getAssessmentProgressPort, times(1)).getProgress(any());

        assertEquals(assessmentId, result.id());
        assertEquals(5, result.answersCount());
        assertEquals(10, result.questionsCount());
    }

    @Test
    void testGetAssessmentProgress_InValidAssessmentId() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_PROGRESS)).thenReturn(true);
        when(getAssessmentProgressPort.getProgress(assessmentId))
            .thenThrow(new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentProgress(param));
        Assertions.assertThat(throwable).hasMessage(GET_ASSESSMENT_PROGRESS_ASSESSMENT_NOT_FOUND);
    }

    @Test
    void testGetAssessmentProgress_CurrentUserDoesNotHaveAccessToAssessment_ThrowsAccessDeniedException() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_PROGRESS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentProgress(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
