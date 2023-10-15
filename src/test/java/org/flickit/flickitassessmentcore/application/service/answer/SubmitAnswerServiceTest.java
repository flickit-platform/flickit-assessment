package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.*;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerViewByAssessmentResultAndQuestionPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.AnswerSubmissionNotAllowedException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.SUBMIT_ANSWER_ANSWER_IS_NOT_APPLICABLE_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
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
    private LoadAnswerViewByAssessmentResultAndQuestionPort loadExistAnswerViewPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;


    @Disabled
    @Test
    void testSubmitAnswer_AnswerNotExist_SavesSelectedAnswerOptionIdAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 2L;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, Boolean.FALSE);

        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(false, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(updateAnswerPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNull_SavesAnswerAndDoesntInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        SubmitAnswerUseCase.Param param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, Boolean.FALSE);

        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(false, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verifyNoInteractions(invalidateAssessmentResultPort, updateAnswerPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerWithDifferentAnswerOptionExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = new AssessmentResult(assessmentResultId, null, null);
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long newAnswerOptionId = 2L;
        Long oldAnswerOptionId = 3L;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, newAnswerOptionId, Boolean.FALSE);
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(existAnswerId, oldAnswerOptionId, false));
        assertNotEquals(oldAnswerOptionId, newAnswerOptionId);

        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).loadView(assessmentResultId, questionId);
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(createAnswerPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOptionExist_DoesNotInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 1L;
        Long questionId = 1L;
        Long sameAnswerOptionId = 2L;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, sameAnswerOptionId, Boolean.FALSE);
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(existAnswerId, sameAnswerOptionId, false));
        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).loadView(assessmentResultId, questionId);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerNotExist_SavesNotApplicableAnswerEvenWithSelectedAnswerOptionIdAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);
        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(Optional.empty());

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
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerExists_SavesNotApplicableAnswerEvenWithSelectedAnswerOptionIdAndInvalidatesAssessmentResult() {
        UUID answerId = UUID.randomUUID();
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = 1L;
        Long newAnswerOptionId = 2L;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);
        var answer = new LoadAnswerViewByAssessmentResultAndQuestionPort.Result(answerId, newAnswerOptionId, Boolean.FALSE);
        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(Optional.of(answer));

        service.submitAnswer(param);

        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(createAnswerPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_NotApplicableAnswerExist_ThrowsException() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long optionId = 2L;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, optionId, Boolean.TRUE);
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(existAnswerId, null, Boolean.TRUE));

        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(existAnswer);

        var throwable = assertThrows(AnswerSubmissionNotAllowedException.class, () -> service.submitAnswer(param));
        assertThat(throwable).hasMessage(SUBMIT_ANSWER_ANSWER_IS_NOT_APPLICABLE_MESSAGE);

        verify(loadExistAnswerViewPort, times(1)).loadView(assessmentResultId, questionId);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerNotExist_SavesNotApplicableAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, isNotApplicable);
        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerWithDifferentApplicableExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        Boolean oldIsNotApplicable = Boolean.TRUE;
        Boolean newIsNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, newIsNotApplicable);
        UUID existAnswerId = UUID.randomUUID();
        var existAnswer = Optional.of(new LoadAnswerViewByAssessmentResultAndQuestionPort.Result(existAnswerId, answerOptionId, oldIsNotApplicable));
        assertNotEquals(oldIsNotApplicable, newIsNotApplicable);

        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).loadView(assessmentResultId, questionId);
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(createAnswerPort);
    }

    @Disabled
    @Test
    void testSubmitAnswer_AnswerWithSameIsNotApplicableExist_DoNotInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Long answerOptionId = null;
        Boolean sameIsNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentResultId, questionnaireId, questionId, answerOptionId, sameIsNotApplicable);
        UUID existAnswerId = UUID.randomUUID();
        var existAnswer = Optional.of(new LoadAnswerViewByAssessmentResultAndQuestionPort.Result(existAnswerId, answerOptionId, sameIsNotApplicable));
        when(loadExistAnswerViewPort.loadView(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).loadView(assessmentResultId, questionId);
    }
}
