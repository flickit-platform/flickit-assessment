package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadSubmitAnswerExistAnswerViewByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadSubmitAnswerExistAnswerViewByAssessmentResultAndQuestionPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.AnswerSubmissionNotAllowedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerServiceTest {

    @Spy
    @InjectMocks
    private SubmitAnswerService service;

    @Mock
    private SaveAnswerPort saveAnswerPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerPort;

    @Mock
    private LoadSubmitAnswerExistAnswerViewByAssessmentResultAndQuestionPort loadExistAnswerViewPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Test
    void submitAnswer_AnswerNotExist_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long answerOptionId = 2L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentResultId,
            questionId,
            answerOptionId
        );
        when(loadExistAnswerViewPort.loadView(eq(assessmentResultId), eq(questionId)))
            .thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(saveAnswerPort.persist(any(SaveAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<SaveAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(SaveAnswerPort.Param.class);
        verify(saveAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(true, saveAnswerParam.getValue().isNotApplicable());

        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(eq(assessmentResultId));
        verifyNoInteractions(
            updateAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerNotExistAndOptionIdIsNull_SavesAnswerAndDoesntInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long answerOptionId = null;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentResultId,
            questionId,
            answerOptionId
        );
        when(loadExistAnswerViewPort.loadView(eq(assessmentResultId), eq(questionId)))
            .thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(saveAnswerPort.persist(any(SaveAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<SaveAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(SaveAnswerPort.Param.class);
        verify(saveAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(true, saveAnswerParam.getValue().isNotApplicable());

        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verifyNoInteractions(
            invalidateAssessmentResultPort,
            updateAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerWithDifferentAnswerOptionExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long newAnswerOptionId = 2L;
        Long oldAnswerOptionId = 3L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentResultId,
            questionId,
            newAnswerOptionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            oldAnswerOptionId,
            false
        ));
        assertNotEquals(oldAnswerOptionId, newAnswerOptionId);

        when(loadExistAnswerViewPort.loadView(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerOptionPort.Param> updateParam = ArgumentCaptor.forClass(UpdateAnswerOptionPort.Param.class);
        verify(updateAnswerPort).updateAnswerOptionById(updateParam.capture());
        assertEquals(existAnswerId, updateParam.getValue().id());
        assertEquals(newAnswerOptionId, updateParam.getValue().answerOptionId());

        verify(loadExistAnswerViewPort, times(1)).loadView(eq(assessmentResultId), eq(questionId));
        verify(updateAnswerPort, times(1)).updateAnswerOptionById(any(UpdateAnswerOptionPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(eq(assessmentResultId));
        verifyNoInteractions(
            saveAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerWithSameAnswerOptionExist_DoesntInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long sameAnswerOptionId = 2L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentResultId,
            questionId,
            sameAnswerOptionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            sameAnswerOptionId,
            false
        ));
        when(loadExistAnswerViewPort.loadView(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).loadView(eq(assessmentResultId), eq(questionId));
        verifyNoInteractions(
            saveAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort
        );
    }

    @Test
    void submitAnswer_NotApplicableAnswerExist_throwException() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long optionId = 2L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentResultId,
            questionId,
            optionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            null,
            true
        ));
        when(loadExistAnswerViewPort.loadView(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        assertThrows(AnswerSubmissionNotAllowedException.class, () -> service.submitAnswer(param));

        verify(loadExistAnswerViewPort, times(1)).loadView(eq(assessmentResultId), eq(questionId));
        verifyNoInteractions(
            saveAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort
        );
    }
}
