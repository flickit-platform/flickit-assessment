package org.flickit.flickitassessmentcore.application.service.questionnaire;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.flickitassessmentcore.application.port.out.questionnaire.GetQuestionnairesProgressByAssessmentPort;
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
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private GetQuestionnairesProgressByAssessmentPort getQuestionnairesProgressPort;

    @Test
    void getQuestionnairesProgressTest(){
        UUID assessmentId = UUID.randomUUID();
        Param useCaseParam = new Param(assessmentId);

        service.getQuestionnairesProgress(useCaseParam);

        ArgumentCaptor<UUID> assessmentPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentProgressPort).getAssessmentProgressById(assessmentPortAssessmentId.capture());

        ArgumentCaptor<UUID> questionnairesPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getQuestionnairesProgressPort).getQuestionnairesProgressByAssessmentId(questionnairesPortAssessmentId.capture());

        assertEquals(assessmentId, assessmentPortAssessmentId.getValue());
        assertEquals(assessmentId, questionnairesPortAssessmentId.getValue());

        verify(getAssessmentProgressPort, times(1)).getAssessmentProgressById(any());
        verify(getQuestionnairesProgressPort, times(1)).getQuestionnairesProgressByAssessmentId(any());
    }
}
