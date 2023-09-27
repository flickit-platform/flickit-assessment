package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort.Result;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerIsNotApplicablePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
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
class SubmitAnswerIsNotApplicableServiceTest {

    @InjectMocks
    private SubmitAnswerIsNotApplicableService service;

    @Mock
    private CreateAnswerPort saveAnswerPort;

    @Mock
    private UpdateAnswerIsNotApplicablePort updateIsNotApplicablePort;

    @Mock
    private LoadAnswerIdAndIsNotApplicableByAssessmentResultAndQuestionPort loadAnswerIdAndIsNotApplicablePort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Test
    void submitAnswer_AnswerNotExist_SavesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerIsNotApplicableUseCase.Param(assessmentResultId, questionnaireId, questionId, isNotApplicable);
        when(loadAnswerIdAndIsNotApplicablePort.load(assessmentResultId, questionId)).thenReturn(Optional.empty());

        UUID savedAnswerId = UUID.randomUUID();
        when(saveAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswerIsNotApplicable(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(saveAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResultId, saveAnswerParam.getValue().assessmentResultId());
        assertEquals(questionnaireId, saveAnswerParam.getValue().questionnaireId());
        assertEquals(questionId, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(saveAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(updateIsNotApplicablePort);
    }

    @Test
    void submitAnswer_AnswerWithDifferentApplicableExist_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Boolean oldIsNotApplicable = Boolean.TRUE;
        Boolean newIsNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerIsNotApplicableUseCase.Param(assessmentResultId, questionnaireId, questionId, newIsNotApplicable);
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(existAnswerId, oldIsNotApplicable));
        assertNotEquals(oldIsNotApplicable, newIsNotApplicable);

        when(loadAnswerIdAndIsNotApplicablePort.load(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswerIsNotApplicable(param);

        ArgumentCaptor<UpdateAnswerIsNotApplicablePort.Param> updateParam = ArgumentCaptor.forClass(UpdateAnswerIsNotApplicablePort.Param.class);
        verify(updateIsNotApplicablePort).update(updateParam.capture());
        assertEquals(existAnswerId, updateParam.getValue().id());
        assertEquals(newIsNotApplicable, updateParam.getValue().isNotApplicable());

        verify(loadAnswerIdAndIsNotApplicablePort, times(1)).load(assessmentResultId, questionId);
        verify(updateIsNotApplicablePort, times(1)).update(any(UpdateAnswerIsNotApplicablePort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResultId);
        verifyNoInteractions(saveAnswerPort);
    }

    @Test
    void submitAnswer_AnswerWithSameIsNotApplicableExist_DoNotInvalidateAssessmentResult() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionnaireId = 25L;
        Long questionId = 1L;
        Boolean sameIsNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerIsNotApplicableUseCase.Param(assessmentResultId, questionnaireId, questionId, sameIsNotApplicable);
        UUID existAnswerId = UUID.randomUUID();
        Optional<Result> existAnswer = Optional.of(new Result(existAnswerId, sameIsNotApplicable));
        when(loadAnswerIdAndIsNotApplicablePort.load(assessmentResultId, questionId)).thenReturn(existAnswer);

        service.submitAnswerIsNotApplicable(param);

        verify(loadAnswerIdAndIsNotApplicablePort, times(1)).load(assessmentResultId, questionId);
        verifyNoInteractions(saveAnswerPort, updateIsNotApplicablePort, invalidateAssessmentResultPort);
    }
}
