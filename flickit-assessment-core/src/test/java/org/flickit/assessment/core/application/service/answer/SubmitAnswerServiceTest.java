package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.ANSWER_QUESTION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerServiceTest {

    private static final Long QUESTIONNAIRE_ID = 25L;
    private static final Long QUESTION_ID = 1L;

    @InjectMocks
    private SubmitAnswerService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionMayNotBeApplicablePort loadQuestionMayNotBeApplicablePort;

    @Mock
    private CreateAnswerPort createAnswerPort;

    @Mock
    private UpdateAnswerPort updateAnswerPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testSubmitAnswer_UserHasNotAccess_ThrowException() {
        UUID assessmentId = UUID.randomUUID();
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, null, ConfidenceLevel.getDefault().getId(), Boolean.TRUE, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.submitAnswer(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAnswerPort,
            createAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNotNull_SavesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID assessmentId = UUID.randomUUID();
        UUID savedAnswerId = UUID.randomUUID();
        Long answerOptionId = 2L;
        Boolean isNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, answerOptionId, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.FALSE, Boolean.FALSE);
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistAndOptionIdIsNullAndIsNotApplicableIsFalse_DontSavesAnswerAndDontInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID assessmentId = UUID.randomUUID();
        Boolean isNotApplicable = Boolean.FALSE;
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, null, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());

        service.submitAnswer(param);

        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerNotExistsAndIsNotApplicableTrue_SavesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID savedAnswerId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Long answerOptionId = 1L;
        Boolean isNotApplicable = Boolean.TRUE;
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, answerOptionId, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);

        service.submitAnswer(param);

        ArgumentCaptor<CreateAnswerPort.Param> saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class), eq(Boolean.FALSE), eq(Boolean.FALSE));
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAnswerOptionChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID assessmentId = UUID.randomUUID();
        Boolean isNotApplicable = Boolean.FALSE;
        Long newAnswerOptionId = AnswerOptionMother.optionTwo().getId();
        AnswerOption oldAnswerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, newAnswerOptionId, ConfidenceLevel.getDefault().getId(), isNotApplicable, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(newAnswerOptionId, updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.FALSE, Boolean.TRUE);
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsAndIsNotApplicableTrue_SavesAndInvalidatesAssessmentResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean isNotApplicable = Boolean.TRUE;
        AnswerOption oldAnswerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, oldAnswerOption.getId(), ConfidenceLevel.getDefault().getId(), isNotApplicable, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, ConfidenceLevel.getDefault().getId(), isNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(any(UUID.class), eq(Boolean.FALSE), eq(Boolean.TRUE));
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOption_DoNotInvalidateAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID assessmentId = UUID.randomUUID();
        AnswerOption sameAnswerOption = AnswerOptionMother.optionTwo();
        Answer existAnswer = AnswerMother.answerWithNullNotApplicable(sameAnswerOption);
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, sameAnswerOption.getId(), ConfidenceLevel.getDefault().getId(), null, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        service.submitAnswer(param);

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            createAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsNotApplicableChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean newIsNotApplicable = Boolean.FALSE;
        AnswerOption answerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableTrue(answerOption);
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, answerOption.getId(), ConfidenceLevel.getDefault().getId(), newIsNotApplicable, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), answerOption.getId(), ConfidenceLevel.getDefault().getId(), newIsNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(newIsNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.FALSE, Boolean.TRUE);
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerIsNotApplicableAndQuestionNotMayNotBeApplicable_ThrowsException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Boolean newIsNotApplicable = Boolean.TRUE;
        AnswerOption answerOption = AnswerOptionMother.optionOne();
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, answerOption.getId(), ConfidenceLevel.getDefault().getId(), newIsNotApplicable, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> service.submitAnswer(param));
        assertEquals(SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE, exception.getMessageKey());

        verifyNoInteractions(createAnswerPort,
            createAnswerPort,
            updateAnswerPort,
            loadAnswerPort,
            invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameIsNotApplicableExists_DoNotInvalidateAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID assessmentId = UUID.randomUUID();
        Answer existAnswer = AnswerMother.answerWithNotApplicableTrue(null);
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, null, ConfidenceLevel.getDefault().getId(), Boolean.TRUE, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        service.submitAnswer(param);

        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(any());
        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verifyNoInteractions(createAnswerPort, updateAnswerPort, invalidateAssessmentResultPort);
    }

    @Test
    void testSubmitAnswer_AnswerExistsConfidenceLevelChanged_UpdatesAnswerAndInvalidatesAssessmentResult() {
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        UUID assessmentId = UUID.randomUUID();
        Boolean isNotApplicable = Boolean.FALSE;
        Integer newConfidenceLevelId = 3;
        AnswerOption answerOption = AnswerOptionMother.optionOne();
        Answer existAnswer = AnswerMother.answerWithNotApplicableFalse(answerOption);
        var param = new SubmitAnswerUseCase.Param(assessmentId, QUESTIONNAIRE_ID, QUESTION_ID, answerOption.getId(), newConfidenceLevelId, isNotApplicable, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        service.submitAnswer(param);

        ArgumentCaptor<UpdateAnswerPort.Param> updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(newConfidenceLevelId, updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(assessmentResult.getId(), Boolean.TRUE, Boolean.FALSE);
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort);
    }
}
