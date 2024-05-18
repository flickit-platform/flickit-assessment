package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionnaireAnswerListPort;
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
    private LoadQuestionnaireAnswerListPort loadQuestionnaireAnswerListPort;

    @Test
    void testGetAnswerList() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 123L;
        int size = 50;
        int page = 0;
        GetAnswerListUseCase.Param param = new GetAnswerListUseCase.Param(assessmentId, questionnaireId, size, page);

        var answerItems = Arrays.asList(
            new Answer(UUID.randomUUID(), new AnswerOption(1L, 0, "option", 1L, null), 1L, ConfidenceLevel.getDefault().getId(), Boolean.FALSE),
            new Answer(UUID.randomUUID(), new AnswerOption(1L, 0, "option", 1L, null), 1L, ConfidenceLevel.getDefault().getId(), Boolean.FALSE)
        );
        PaginatedResponse<Answer> mockResult = new PaginatedResponse<>(answerItems, page, size, null, null, 2);
        when(loadQuestionnaireAnswerListPort.loadQuestionnaireAnswers(assessmentId, questionnaireId, size, page))
            .thenReturn(mockResult);

        PaginatedResponse<AnswerListItem> result = getAnswerListService.getAnswerList(param);

        ArgumentCaptor<UUID> assessmentIdPortCapture = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Long> questionnaireIdPortCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> sizePortCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pagePortCapture = ArgumentCaptor.forClass(Integer.class);
        verify(loadQuestionnaireAnswerListPort).loadQuestionnaireAnswers(assessmentIdPortCapture.capture(),
            questionnaireIdPortCapture.capture(),
            sizePortCapture.capture(),
            pagePortCapture.capture());

        assertEquals(assessmentId, assessmentIdPortCapture.getValue());
        assertEquals(questionnaireId, questionnaireIdPortCapture.getValue());
        assertEquals(size, sizePortCapture.getValue());
        assertEquals(page, pagePortCapture.getValue());
        assertNotNull(result.getItems());
        assertEquals(answerItems.size(), result.getItems().size());
        verify(loadQuestionnaireAnswerListPort, times(1))
            .loadQuestionnaireAnswers(any(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void testGetAnswerList_EmptyResult() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 125L;
        int size = 50;
        int page = 0;
        GetAnswerListUseCase.Param param = new GetAnswerListUseCase.Param(assessmentId, questionnaireId, size, page);

        List<Answer> answerItems = Collections.emptyList();
        PaginatedResponse<Answer> mockResult = new PaginatedResponse<>(answerItems, page, 50, null, null, 2);
        when(loadQuestionnaireAnswerListPort.loadQuestionnaireAnswers(assessmentId, questionnaireId, size, page))
            .thenReturn(mockResult);

        PaginatedResponse<AnswerListItem> result = getAnswerListService.getAnswerList(param);

        ArgumentCaptor<UUID> assessmentIdPortCapture = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Long> questionnaireIdPortCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> sizePortCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pagePortCapture = ArgumentCaptor.forClass(Integer.class);
        verify(loadQuestionnaireAnswerListPort).loadQuestionnaireAnswers(assessmentIdPortCapture.capture(),
            questionnaireIdPortCapture.capture(),
            sizePortCapture.capture(),
            pagePortCapture.capture());

        assertEquals(assessmentId, assessmentIdPortCapture.getValue());
        assertEquals(questionnaireId, questionnaireIdPortCapture.getValue());
        assertEquals(size, sizePortCapture.getValue());
        assertEquals(page, pagePortCapture.getValue());
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        verify(loadQuestionnaireAnswerListPort, times(1)).loadQuestionnaireAnswers(any(), anyLong(), anyInt(), anyInt());
    }
}
