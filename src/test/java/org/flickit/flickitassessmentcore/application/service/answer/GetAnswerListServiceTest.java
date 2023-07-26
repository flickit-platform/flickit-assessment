package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadAnswersByAssessmentAndQuestionnaireIdPort;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAnswerListServiceTest {

    @InjectMocks
    private GetAnswerListService getAnswerListService;

    @Mock
    private LoadAnswersByAssessmentAndQuestionnaireIdPort loadAnswersPort;

    @Test
    public void testGetAnswerList() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 123L;
        GetAnswerListUseCase.Param param = new GetAnswerListUseCase.Param(assessmentId, questionnaireId);

        List<Answer> mockAnswers = Arrays.asList(
            new Answer(),
            new Answer()
        );
        when(loadAnswersPort.loadAnswersByAssessmentAndQuestionnaireIdPort(any())).thenReturn(mockAnswers);

        GetAnswerListUseCase.Result result = getAnswerListService.getAnswerList(param);

        ArgumentCaptor<LoadAnswersByAssessmentAndQuestionnaireIdPort.Param> loadPortParam = ArgumentCaptor
            .forClass(LoadAnswersByAssessmentAndQuestionnaireIdPort.Param.class);
        verify(loadAnswersPort).loadAnswersByAssessmentAndQuestionnaireIdPort(loadPortParam.capture());

        assertEquals(assessmentId, loadPortParam.getValue().assessmentId());
        assertEquals(questionnaireId, loadPortParam.getValue().questionnaireId());
        assertNotNull(result);
        assertEquals(mockAnswers, result.answers());
        verify(loadAnswersPort, times(1)).loadAnswersByAssessmentAndQuestionnaireIdPort(any());
    }

    @Test
    public void testGetAnswerList_EmptyResult() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 125L;
        GetAnswerListUseCase.Param param = new GetAnswerListUseCase.Param(assessmentId, questionnaireId);

        when(loadAnswersPort.loadAnswersByAssessmentAndQuestionnaireIdPort(any())).thenReturn(Collections.emptyList());

        GetAnswerListUseCase.Result result = getAnswerListService.getAnswerList(param);

        ArgumentCaptor<LoadAnswersByAssessmentAndQuestionnaireIdPort.Param> loadPortParam = ArgumentCaptor
            .forClass(LoadAnswersByAssessmentAndQuestionnaireIdPort.Param.class);
        verify(loadAnswersPort).loadAnswersByAssessmentAndQuestionnaireIdPort(loadPortParam.capture());

        assertEquals(assessmentId, loadPortParam.getValue().assessmentId());
        assertEquals(questionnaireId, loadPortParam.getValue().questionnaireId());
        assertNotNull(result);
        assertTrue(result.answers().isEmpty());
        verify(loadAnswersPort, times(1)).loadAnswersByAssessmentAndQuestionnaireIdPort(any());
    }

}
