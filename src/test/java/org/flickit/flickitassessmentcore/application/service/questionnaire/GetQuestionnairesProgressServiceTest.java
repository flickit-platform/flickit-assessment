package org.flickit.flickitassessmentcore.application.service.questionnaire;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.QuestionnaireProgress;
import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Result;
import org.flickit.flickitassessmentcore.application.port.out.questionnaire.GetQuestionnairesProgressPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetQuestionnairesProgressServiceTest {

    @InjectMocks
    private GetQuestionnairesProgressService service;

    @Mock
    private GetQuestionnairesProgressPort getQuestionnairesProgressPort;

    @Test
    void getQuestionnairesProgressTest(){
        UUID assessmentId = UUID.randomUUID();
        Param useCaseParam = new Param(assessmentId);

        List<QuestionnaireProgress> expectedQProgresses = Arrays.asList(new QuestionnaireProgress(1L, 15),
            new QuestionnaireProgress(2L, 17));
        when(getQuestionnairesProgressPort.getQuestionnairesProgressByAssessmentId(assessmentId)).thenReturn(expectedQProgresses);

        Result questionnairesProgress = service.getQuestionnairesProgress(useCaseParam);

        ArgumentCaptor<UUID> portArgAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getQuestionnairesProgressPort).getQuestionnairesProgressByAssessmentId(portArgAssessmentId.capture());

        assertEquals(expectedQProgresses.size(), questionnairesProgress.questionnairesProgress().size());
        for (int progressIndex = 0; progressIndex < expectedQProgresses.size(); progressIndex++) {
            QuestionnaireProgress expected = expectedQProgresses.get(progressIndex);
            QuestionnaireProgress result = questionnairesProgress.questionnairesProgress().get(progressIndex);
            assertEquals(expected.id(), result.id());
            assertEquals(expected.answersCount(), result.answersCount());
        }
        assertEquals(assessmentId, portArgAssessmentId.getValue());

        verify(getQuestionnairesProgressPort, times(1)).getQuestionnairesProgressByAssessmentId(any());
    }
}
