package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerCommand;
import org.flickit.flickitassessmentcore.application.port.out.answer.CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    private LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort loadAnswerIdAndOptionIdPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Mock
    private CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort checkAnswerExistencePort;

    @Test
    void submitAnswer_AnswerNotExist_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long answerOptionId = 2L;
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            assessmentResultId,
            questionId,
            answerOptionId
        );
        when(checkAnswerExistencePort.existsByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId))).thenReturn(false);

        UUID savedAnswerId = UUID.randomUUID();
        when(saveAnswerPort.persist(any(SaveAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(command);

        ArgumentCaptor<SaveAnswerPort.Param> param = ArgumentCaptor.forClass(SaveAnswerPort.Param.class);
        verify(saveAnswerPort).persist(param.capture());
        assertEquals(assessmentResultId, param.getValue().assessmentResultId());
        assertEquals(questionId, param.getValue().questionId());
        assertEquals(answerOptionId, param.getValue().answerOptionId());

        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(eq(assessmentResultId));
        verifyNoInteractions(
            updateAnswerPort,
            loadAnswerIdAndOptionIdPort
        );
    }

    @Test
    void submitAnswer_AnswerWithDifferentAnswerOptionExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long newAnswerOptionId = 2L;
        Long oldAnswerOptionId = 3L;
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            assessmentResultId,
            questionId,
            newAnswerOptionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Result existAnswer = new Result(
            existAnswerId,
            oldAnswerOptionId
        );
        assertNotEquals(oldAnswerOptionId, newAnswerOptionId);

        when(checkAnswerExistencePort.existsByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId))).thenReturn(true);
        when(loadAnswerIdAndOptionIdPort.loadByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswer(command);

        ArgumentCaptor<UpdateAnswerOptionPort.Param> param = ArgumentCaptor.forClass(UpdateAnswerOptionPort.Param.class);
        verify(updateAnswerPort).updateAnswerOptionById(param.capture());
        assertEquals(existAnswerId, param.getValue().id());
        assertEquals(newAnswerOptionId, param.getValue().answerOptionId());

        verify(loadAnswerIdAndOptionIdPort, times(1)).loadByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId));
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
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            assessmentResultId,
            questionId,
            sameAnswerOptionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Result existAnswer = new Result(
            existAnswerId,
            sameAnswerOptionId
        );
        when(checkAnswerExistencePort.existsByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId))).thenReturn(true);
        when(loadAnswerIdAndOptionIdPort.loadByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId))).thenReturn(existAnswer);

        service.submitAnswer(command);

        verify(loadAnswerIdAndOptionIdPort, times(1)).loadByAssessmentResultIdAndQuestionId(eq(assessmentResultId), eq(questionId));
        verifyNoInteractions(
            saveAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort
        );
    }
}
