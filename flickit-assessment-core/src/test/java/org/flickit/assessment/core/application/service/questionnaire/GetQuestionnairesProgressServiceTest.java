package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Param;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.QuestionnaireProgress;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Result;
import org.flickit.assessment.core.application.port.out.questionnaire.GetQuestionnairesProgressPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRES_PROGRESS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetQuestionnairesProgressServiceTest {

    @InjectMocks
    private GetQuestionnairesProgressService service;

    @Mock
    private GetQuestionnairesProgressPort getQuestionnairesProgressPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetQuestionnairesProgressTest() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        Param useCaseParam = new Param(assessmentId, currentUserId);

        List<QuestionnaireProgress> expectedQProgresses = Arrays.asList(new QuestionnaireProgress(1L, 15, 2),
            new QuestionnaireProgress(2L, 17, 3));
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_QUESTIONNAIRES_PROGRESS)).thenReturn(true);
        when(getQuestionnairesProgressPort.getQuestionnairesProgressByAssessmentId(assessmentId)).thenReturn(expectedQProgresses);

        Result result = service.getQuestionnairesProgress(useCaseParam);

        ArgumentCaptor<UUID> portArgAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getQuestionnairesProgressPort).getQuestionnairesProgressByAssessmentId(portArgAssessmentId.capture());
        assertEquals(assessmentId, portArgAssessmentId.getValue());

        verify(assessmentAccessChecker, times(1)).isAuthorized(useCaseParam.getAssessmentId(), useCaseParam.getCurrentUserId(), VIEW_QUESTIONNAIRES_PROGRESS);
        verify(getQuestionnairesProgressPort, times(1)).getQuestionnairesProgressByAssessmentId(any());

        assertNotNull(result.questionnairesProgress());
        assertEquals(expectedQProgresses.size(), result.questionnairesProgress().size());
        for (int i = 0; i < expectedQProgresses.size(); i++) {
            assertEquals(expectedQProgresses.get(i).id(), result.questionnairesProgress().get(i).id());
            assertEquals(expectedQProgresses.get(i).answersCount(), result.questionnairesProgress().get(i).answersCount());
        }
    }

    @Test
    void testGetQuestionnairesProgress_UserIsNotAuthorized_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        Param useCaseParam = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_QUESTIONNAIRES_PROGRESS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionnairesProgress(useCaseParam));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker, times(1))
            .isAuthorized(useCaseParam.getAssessmentId(), useCaseParam.getCurrentUserId(), VIEW_QUESTIONNAIRES_PROGRESS);
        verify(getQuestionnairesProgressPort, never()).getQuestionnairesProgressByAssessmentId(any());
    }
}
