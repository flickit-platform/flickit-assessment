package org.flickit.assessment.core.application.service.questionnaire;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetQuestionnairesProgressServiceTest {

    @InjectMocks
    private GetQuestionnairesProgressService service;

    @Mock
    private GetQuestionnairesProgressPort getQuestionnairesProgressPort;

    @Test
    void testGetQuestionnairesProgressTest(){
        UUID assessmentId = UUID.randomUUID();
        Param useCaseParam = new Param(assessmentId);

        List<QuestionnaireProgress> expectedQProgresses = Arrays.asList(new QuestionnaireProgress(1L, 15, 2),
            new QuestionnaireProgress(2L, 17, 3));
        when(getQuestionnairesProgressPort.getQuestionnairesProgressByAssessmentId(assessmentId)).thenReturn(expectedQProgresses);

        Result result = service.getQuestionnairesProgress(useCaseParam);

        ArgumentCaptor<UUID> portArgAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getQuestionnairesProgressPort).getQuestionnairesProgressByAssessmentId(portArgAssessmentId.capture());
        assertEquals(assessmentId, portArgAssessmentId.getValue());

        verify(getQuestionnairesProgressPort, times(1)).getQuestionnairesProgressByAssessmentId(any());

        assertNotNull(result.questionnairesProgress());
        assertEquals(expectedQProgresses.size(), result.questionnairesProgress().size());
        for (int i = 0; i < expectedQProgresses.size(); i++) {
            assertEquals(expectedQProgresses.get(i).id(), result.questionnairesProgress().get(i).id());
            assertEquals(expectedQProgresses.get(i).answersCount(), result.questionnairesProgress().get(i).answersCount());
        }
    }
}
