package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerServiceTest {

    @InjectMocks
    private SubmitAnswerService service;

    @Mock
    private CreateAnswerPort createAnswerPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Test
    void submitAnswer_AnswerNotExist_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = new AssessmentResult(assessmentResultId, null, null);
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 2L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentId,
            questionnaireId,
            questionId,
            answerOptionId
        );
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.loadAnswerIdAndOptionId(assessmentResultId, questionId))
            .thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(
            updateAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerWithDifferentAnswerOptionExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = new AssessmentResult(assessmentResultId, null, null);
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long newAnswerOptionId = 2L;
        Long oldAnswerOptionId = 3L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentId,
            questionnaireId,
            questionId,
            newAnswerOptionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            oldAnswerOptionId
        ));
        assertNotEquals(oldAnswerOptionId, newAnswerOptionId);

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.loadAnswerIdAndOptionId(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerOptionPort.Param> updateParam = ArgumentCaptor.forClass(UpdateAnswerOptionPort.Param.class);
        verify(updateAnswerPort).updateAnswerOptionById(updateParam.capture());
        assertEquals(existAnswerId, updateParam.getValue().id());
        assertEquals(newAnswerOptionId, updateParam.getValue().answerOptionId());

        verify(loadAnswerPort, times(1)).loadAnswerIdAndOptionId(assessmentResultId, questionId);
        verify(updateAnswerPort, times(1)).updateAnswerOptionById(any(UpdateAnswerOptionPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(
            createAnswerPort
        );
    }

    @Test
    void submitAnswer_AnswerWithSameAnswerOptionExist_DoesntInvalidateAssessmentResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = new AssessmentResult(assessmentResultId, null, null);
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long sameAnswerOptionId = 2L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentId,
            questionnaireId,
            questionId,
            sameAnswerOptionId
        );
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(
            existAnswerId,
            sameAnswerOptionId
        ));
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.loadAnswerIdAndOptionId(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadAnswerPort, times(1)).loadAnswerIdAndOptionId(assessmentResultId, questionId);
        verifyNoInteractions(
            createAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort
        );
    }

    @Test
    void submitAnswer_AssessmentResultNotFound_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long sameAnswerOptionId = 2L;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(
            assessmentId,
            questionnaireId,
            questionId,
            sameAnswerOptionId
        );
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.submitAnswer(param));

        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(assessmentId);
        verifyNoInteractions(
            loadAnswerPort,
            createAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort
        );
    }
}
