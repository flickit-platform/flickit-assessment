package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswersByQuestionnaireIdPort;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAnswerListServiceTest {

    @InjectMocks
    private GetAnswerListService getAnswerListService;

    @Mock
    private LoadAnswersByQuestionnaireIdPort loadAnswersPort;

    @Test
    void testGetAnswerList() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 123L;
        int size = 50;
        int page = 0;
        GetAnswerListUseCase.Param param = new GetAnswerListUseCase.Param(assessmentId, questionnaireId, size, page);

        List<AnswerListItem> answerItems = Arrays.asList(
            new AnswerListItem(UUID.randomUUID(), 1L, 1L, ConfidenceLevel.getDefault(), Boolean.FALSE),
            new AnswerListItem(UUID.randomUUID(), 1L, 1L, ConfidenceLevel.getDefault(), Boolean.FALSE)
        );
        PaginatedResponse<AnswerListItem> mockResult = new PaginatedResponse<>(answerItems, page, size, null, null, 2);
        when(loadAnswersPort.loadAnswersByQuestionnaireId(any())).thenReturn(mockResult);

        PaginatedResponse<AnswerListItem> result = getAnswerListService.getAnswerList(param);

        ArgumentCaptor<LoadAnswersByQuestionnaireIdPort.Param> loadPortParam = ArgumentCaptor
            .forClass(LoadAnswersByQuestionnaireIdPort.Param.class);
        verify(loadAnswersPort).loadAnswersByQuestionnaireId(loadPortParam.capture());

        assertEquals(assessmentId, loadPortParam.getValue().assessmentId());
        assertEquals(questionnaireId, loadPortParam.getValue().questionnaireId());
        assertEquals(size, loadPortParam.getValue().size());
        assertEquals(page, loadPortParam.getValue().page());
        assertNotNull(result.getItems());
        assertEquals(answerItems, result.getItems());
        verify(loadAnswersPort, times(1)).loadAnswersByQuestionnaireId(any());
    }

    @Test
    void testGetAnswerList_EmptyResult() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 125L;
        int size = 50;
        int page = 0;
        GetAnswerListUseCase.Param param = new GetAnswerListUseCase.Param(assessmentId, questionnaireId, size, page);

        List<AnswerListItem> answerItems = Collections.emptyList();
        PaginatedResponse<AnswerListItem> mockResult = new PaginatedResponse<>(answerItems, page, 50, null, null, 2);
        when(loadAnswersPort.loadAnswersByQuestionnaireId(any())).thenReturn(mockResult);

        PaginatedResponse<AnswerListItem> result = getAnswerListService.getAnswerList(param);

        ArgumentCaptor<LoadAnswersByQuestionnaireIdPort.Param> loadPortParam = ArgumentCaptor
            .forClass(LoadAnswersByQuestionnaireIdPort.Param.class);
        verify(loadAnswersPort).loadAnswersByQuestionnaireId(loadPortParam.capture());

        assertEquals(assessmentId, loadPortParam.getValue().assessmentId());
        assertEquals(questionnaireId, loadPortParam.getValue().questionnaireId());
        assertEquals(size, loadPortParam.getValue().size());
        assertEquals(page, loadPortParam.getValue().page());
        assertNotNull(result.getItems());
        assertEquals(answerItems, result.getItems());
        verify(loadAnswersPort, times(1)).loadAnswersByQuestionnaireId(any());
    }

}
