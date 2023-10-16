package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerServiceTest {

    @InjectMocks
    private SubmitAnswerService service;

    @Mock
    private CreateAnswerPort createAnswerPort;

    @Mock
    private UpdateAnswerPort updateAnswerPort;

    @Mock
    private LoadAnswerPort loadExistAnswerViewPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;


    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNotNull_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 2L;
        Boolean isNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);

        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable.booleanValue(), saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNull_SavesAnswerAndDoesNotInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        Boolean isNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);

        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable.booleanValue(), saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verifyNoInteractions(invalidateAssessmentResultPort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistsAndIsNotApplicableTrue_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);
        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAnswerOptionChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long newAnswerOptionId = 2L;
        Long oldAnswerOptionId = 3L;
        Boolean isNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, newAnswerOptionId, isNotApplicable);

        UUID existAnswerId = UUID.randomUUID();
        var existAnswer = Optional.of(new Result(existAnswerId, oldAnswerOptionId, isNotApplicable));
        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResultId, questionId);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAndIsNotApplicableTrue_SavesAndInvalidatesAssessmentResult() {
        UUID answerId = UUID.randomUUID();
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 1L;
        Long newAnswerOptionId = 2L;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);
        var answer = new LoadAnswerPort.Result(answerId, newAnswerOptionId, Boolean.FALSE);
        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(Optional.of(answer));

        var updateParam = new UpdateAnswerPort.Param(answerId, null, isNotApplicable);
        doNothing().when(updateAnswerPort).update(updateParam);

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(answerId, updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOption_DoNotInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long sameAnswerOptionId = 2L;
        Boolean isNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, sameAnswerOptionId, isNotApplicable);

        UUID existAnswerId = UUID.randomUUID();
        var existAnswer = Optional.of(new Result(existAnswerId, sameAnswerOptionId, isNotApplicable));
        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResultId, questionId);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsNotApplicableChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        Boolean oldIsNotApplicable = Boolean.TRUE;
        Boolean newIsNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, newIsNotApplicable);

        UUID existAnswerId = UUID.randomUUID();
        var existAnswer = Optional.of(new LoadAnswerPort.Result(existAnswerId, answerOptionId, oldIsNotApplicable));
        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(existAnswer);

        var updateParam = new UpdateAnswerPort.Param(existAnswerId, answerOptionId, newIsNotApplicable);
        doNothing().when(updateAnswerPort).update(updateParam);

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswerId, updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(newIsNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResultId, questionId);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameIsNotApplicableExists_DoNotInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        Boolean sameIsNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, sameIsNotApplicable);

        UUID existAnswerId = UUID.randomUUID();
        var existAnswer = Optional.of(new LoadAnswerPort.Result(existAnswerId, answerOptionId, sameIsNotApplicable));
        when(loadExistAnswerViewPort.load(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResultId, questionId);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }
}
