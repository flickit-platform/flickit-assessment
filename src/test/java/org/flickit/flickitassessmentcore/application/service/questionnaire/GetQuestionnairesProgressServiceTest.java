package org.flickit.flickitassessmentcore.application.service.questionnaire;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.out.questionnaire.GetQuestionnairesProgressPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

        service.getQuestionnairesProgress(useCaseParam);

        ArgumentCaptor<UUID> portArgAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getQuestionnairesProgressPort).getQuestionnairesProgressByAssessmentId(portArgAssessmentId.capture());

        assertEquals(assessmentId, portArgAssessmentId.getValue());

        verify(getQuestionnairesProgressPort, times(1)).getQuestionnairesProgressByAssessmentId(any());
    }
}
