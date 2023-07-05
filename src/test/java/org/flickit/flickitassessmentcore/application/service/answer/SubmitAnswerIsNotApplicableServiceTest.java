package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerIsNotApplicablePort;
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
class SubmitAnswerIsNotApplicableServiceTest {

    @Spy
    @InjectMocks
    private SubmitAnswerIsNotApplicableService service;

    @Mock
    private SaveAnswerPort saveAnswerPort;

    @Mock
    private UpdateAnswerIsNotApplicablePort updateIsNotApplicablePort;

    @Mock
    private LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort loadAnswerIdAndIsNotApplicablePort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Test
    void submitAnswer_AnswerNotExist_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;
        SubmitAnswerIsNotApplicableUseCase.Param param = new SubmitAnswerIsNotApplicableUseCase.Param(
            assessmentResultId,
            questionId,
            isNotApplicable
        );
        when(loadAnswerIdAndIsNotApplicablePort.loadAnswerIdAndIsNotApplicable(eq(assessmentResultId), eq(questionId)))
            .thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(saveAnswerPort.persist(any(SaveAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswerIsNotApplicable(param);

        ArgumentCaptor<SaveAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(SaveAnswerPort.Param.class);
        verify(saveAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(
            updateIsNotApplicablePort
        );
    }

    @Test
    void submitAnswer_AnswerWithDifferentApplicableExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Boolean oldIsNotApplicable = Boolean.TRUE;
        Boolean newIsNotApplicable = Boolean.FALSE;
        SubmitAnswerIsNotApplicableUseCase.Param param = new SubmitAnswerIsNotApplicableUseCase.Param(
            assessmentResultId,
            questionId,
            newIsNotApplicable
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            oldIsNotApplicable
        ));
        assertNotEquals(oldIsNotApplicable, newIsNotApplicable);

        when(loadAnswerIdAndIsNotApplicablePort.loadAnswerIdAndIsNotApplicable(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswerIsNotApplicable(param);

        ArgumentCaptor<UpdateAnswerIsNotApplicablePort.Param> updateParam = ArgumentCaptor.forClass(UpdateAnswerIsNotApplicablePort.Param.class);
        verify(updateIsNotApplicablePort).updateAnswerIsNotApplicableAndRemoveOptionById(updateParam.capture());
        assertEquals(existAnswerId, updateParam.getValue().id());
        assertEquals(newIsNotApplicable, updateParam.getValue().isNotApplicable());

        verify(loadAnswerIdAndIsNotApplicablePort, times(1)).loadAnswerIdAndIsNotApplicable(eq(assessmentResultId), eq(questionId));
        verify(updateIsNotApplicablePort, times(1)).updateAnswerIsNotApplicableAndRemoveOptionById(any(UpdateAnswerIsNotApplicablePort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(eq(assessmentResultId));
        verifyNoInteractions(
            saveAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerWithSameIsNotApplicableExist_DontInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Boolean sameIsNotApplicable = Boolean.TRUE;
        SubmitAnswerIsNotApplicableUseCase.Param param = new SubmitAnswerIsNotApplicableUseCase.Param(
            assessmentResultId,
            questionId,
            sameIsNotApplicable
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            sameIsNotApplicable
        ));
        when(loadAnswerIdAndIsNotApplicablePort.loadAnswerIdAndIsNotApplicable(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswerIsNotApplicable(param);

        verify(loadAnswerIdAndIsNotApplicablePort, times(1)).loadAnswerIdAndIsNotApplicable(eq(assessmentResultId), eq(questionId));
        verifyNoInteractions(
            saveAnswerPort,
            updateIsNotApplicablePort,
            invalidateAssessmentResultPort
        );
    }
}
