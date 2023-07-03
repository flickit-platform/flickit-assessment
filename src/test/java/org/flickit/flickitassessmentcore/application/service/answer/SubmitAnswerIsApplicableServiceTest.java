package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.*;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
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
class SubmitAnswerIsApplicableServiceTest {

    @Spy
    @InjectMocks
    private SubmitAnswerIsApplicableService service;

    @Mock
    private SaveAnswerPort saveAnswerPort;

    @Mock
    private UpdateAnswerIsApplicablePort updateIsApplicablePort;

    @Mock
    private LoadAnswerIdAndIsApplicableByAssessmentResultAndQuestionPort loadAnswerIdAndIsApplicablePort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Test
    void submitAnswer_AnswerNotExist_SavesAnswerAndDontInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Boolean isApplicable = Boolean.FALSE;
        SubmitAnswerIsApplicableUseCase.Param param = new SubmitAnswerIsApplicableUseCase.Param(
            assessmentResultId,
            questionId,
            isApplicable
        );
        when(loadAnswerIdAndIsApplicablePort.loadAnswerIdAndIsApplicable(eq(assessmentResultId), eq(questionId)))
            .thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(saveAnswerPort.persist(any(SaveAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswerIsApplicable(param);

        ArgumentCaptor<SaveAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(SaveAnswerPort.Param.class);
        verify(saveAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(isApplicable, saveAnswerParam.getValue().isApplicable());

        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verifyNoInteractions(
            invalidateAssessmentResultPort,
            updateIsApplicablePort
        );
    }

    @Test
    void submitAnswer_AnswerWithDifferentApplicableExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Boolean oldIsApplicable = Boolean.FALSE;
        Boolean newIsApplicable = Boolean.TRUE;
        SubmitAnswerIsApplicableUseCase.Param param = new SubmitAnswerIsApplicableUseCase.Param(
            assessmentResultId,
            questionId,
            newIsApplicable
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            oldIsApplicable
        ));
        assertNotEquals(oldIsApplicable, newIsApplicable);

        when(loadAnswerIdAndIsApplicablePort.loadAnswerIdAndIsApplicable(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswerIsApplicable(param);

        ArgumentCaptor<UpdateAnswerIsApplicablePort.Param> updateParam = ArgumentCaptor.forClass(UpdateAnswerIsApplicablePort.Param.class);
        verify(updateIsApplicablePort).updateAnswerIsApplicableAndRemoveOptionById(updateParam.capture());
        assertEquals(existAnswerId, updateParam.getValue().id());
        assertEquals(newIsApplicable, updateParam.getValue().isApplicable());

        verify(loadAnswerIdAndIsApplicablePort, times(1)).loadAnswerIdAndIsApplicable(eq(assessmentResultId), eq(questionId));
        verify(updateIsApplicablePort, times(1)).updateAnswerIsApplicableAndRemoveOptionById(any(UpdateAnswerIsApplicablePort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(eq(assessmentResultId));
        verifyNoInteractions(
            saveAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerWithSameIsApplicableExist_DontInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Boolean sameIsApplicable = Boolean.FALSE;
        SubmitAnswerIsApplicableUseCase.Param param = new SubmitAnswerIsApplicableUseCase.Param(
            assessmentResultId,
            questionId,
            sameIsApplicable
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            sameIsApplicable
        ));
        when(loadAnswerIdAndIsApplicablePort.loadAnswerIdAndIsApplicable(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswerIsApplicable(param);

        verify(loadAnswerIdAndIsApplicablePort, times(1)).loadAnswerIdAndIsApplicable(eq(assessmentResultId), eq(questionId));
        verifyNoInteractions(
            saveAnswerPort,
            updateIsApplicablePort,
            invalidateAssessmentResultPort
        );
    }
}
