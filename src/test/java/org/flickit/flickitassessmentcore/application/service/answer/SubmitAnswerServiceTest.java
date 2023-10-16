package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.domain.Answer;
import org.flickit.flickitassessmentcore.application.domain.AnswerOption;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.mother.AnswerMother;
import org.flickit.flickitassessmentcore.application.domain.mother.AnswerOptionMother;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentResultMother;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
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
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAnswerPort createAnswerPort;

    @Mock
    private UpdateAnswerPort updateAnswerPort;

    @Mock
    private LoadAnswerPort loadExistAnswerViewPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    private static final Long QUESTIONNAIRE_ID = 25L;
    private static final Long QUESTION_ID = 1L;


    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNotNull_SavesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        Long answerOptionId = 2L;
        Boolean isNotApplicable = Boolean.FALSE;

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, answerOptionId, isNotApplicable);
        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId());
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNull_SavesAnswerAndDoesNotInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        Boolean isNotApplicable = Boolean.FALSE;

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, null, isNotApplicable);
        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verifyNoInteractions(invalidateAssessmentResultPort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistsAndIsNotApplicableTrue_SavesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        Long answerOptionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, answerOptionId, isNotApplicable);
        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAnswerOptionChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.FALSE;
        Long newAnswerOptionId = AnswerOptionMother.optionTwo().getId();
        AnswerOption oldAnswerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answer(oldAnswerOption, isNotApplicable);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, newAnswerOptionId, isNotApplicable);
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(newAnswerOptionId, updateAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAndIsNotApplicableTrue_SavesAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.TRUE;
        AnswerOption oldAnswerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answer(oldAnswerOption, isNotApplicable);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, isNotApplicable);
        doNothing().when(updateAnswerPort).update(updateParam);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, oldAnswerOption.getId(), isNotApplicable);
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class));
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOption_DoNotInvalidateAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.FALSE;
        AnswerOption sameAnswerOption = AnswerOptionMother.optionTwo();
        Answer existAnswer = AnswerMother.answer(sameAnswerOption, isNotApplicable);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTIONNAIRE_ID)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTIONNAIRE_ID, sameAnswerOption.getId(), isNotApplicable);
        service.submitAnswer(param);

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResult.getId(), QUESTIONNAIRE_ID);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsNotApplicableChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean oldIsNotApplicable = Boolean.TRUE;
        Boolean newIsNotApplicable = Boolean.FALSE;
        AnswerOption answerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answer(answerOption, oldIsNotApplicable);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), answerOption.getId(), newIsNotApplicable);
        doNothing().when(updateAnswerPort).update(updateParam);

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, answerOption.getId(), newIsNotApplicable);
        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(newIsNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadExistAnswerViewPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameIsNotApplicableExists_DoNotInvalidateAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean sameIsNotApplicable = Boolean.TRUE;
        Answer existAnswer = AnswerMother.answer(null, sameIsNotApplicable);

        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadExistAnswerViewPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var param = new SubmitAnswerUseCase.Param(assessmentResult.getId(), QUESTIONNAIRE_ID, QUESTION_ID, null, sameIsNotApplicable);
        service.submitAnswer(param);

        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(any());
        verify(loadExistAnswerViewPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }
}
