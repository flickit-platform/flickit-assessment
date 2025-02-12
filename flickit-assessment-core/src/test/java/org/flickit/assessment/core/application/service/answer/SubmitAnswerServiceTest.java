package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.HistoryType;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultConfidencePort;
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
import java.util.function.Consumer;

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
    private CreateAnswerHistoryPort createAnswerHistoryPort;

    @Mock
    private UpdateAnswerPort updateAnswerPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private InvalidateAssessmentResultCalculatePort invalidateAssessmentResultCalculatePort;

    @Mock
    private InvalidateAssessmentResultConfidencePort invalidateAssessmentResultConfidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testSubmitAnswer_UserHasNotAccess_ThrowException() {
        UUID assessmentId = UUID.randomUUID();
        var param = createParam(b -> b.assessmentId(assessmentId));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.submitAnswer(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAnswerPort,
            createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_NewApplicableAnswerWithAnswerOptionIdSubmitted_SavesAnswerAndInvalidatesAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = UUID.randomUUID();
        var savedAnswerId = UUID.randomUUID();
        var savedAnswerHistoryId = UUID.randomUUID();
        var answerOptionId = 2L;
        var isNotApplicable = Boolean.FALSE;
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(answerOptionId)
            .isNotApplicable(isNotApplicable));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var result = service.submitAnswer(param);

        assertNotNull(result);
        assertInstanceOf(SubmitAnswerUseCase.Submitted.class, result);
        SubmitAnswerUseCase.Submitted submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(savedAnswerId, submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertEquals(answerOptionId, saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(savedAnswerId, saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(QUESTION_ID, saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNotNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(answerOptionId, saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption().getId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(HistoryType.PERSIST, saveAnswerHistoryParam.getValue().getHistoryType());

        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_NewApplicableAnswerWithNullAnswerOptionIdSubmitted_DontSavesAnswerAndDontInvalidatesAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = UUID.randomUUID();
        var isNotApplicable = Boolean.FALSE;
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(null)
            .isNotApplicable(isNotApplicable));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());

        var result = service.submitAnswer(param);
        assertInstanceOf(SubmitAnswerUseCase.NotAffected.class, result);
        assertNull(result.id());

        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            createAnswerPort,
            createAnswerHistoryPort,
            updateAnswerPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_NewNotApplicableAnswerSubmitted_SavesAnswerAndInvalidatesAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var savedAnswerId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var answerOptionId = 1L;
        var isNotApplicable = Boolean.TRUE;
        var savedAnswerHistoryId = UUID.randomUUID();
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(answerOptionId)
            .isNotApplicable(isNotApplicable));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var result = service.submitAnswer(param);

        assertInstanceOf(SubmitAnswerUseCase.Submitted.class, result);
        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(savedAnswerId, submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var saveAnswerParam = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(saveAnswerParam.capture());
        assertEquals(assessmentResult.getId(), saveAnswerParam.getValue().assessmentResultId());
        assertEquals(QUESTIONNAIRE_ID, saveAnswerParam.getValue().questionnaireId());
        assertEquals(QUESTION_ID, saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerParam.getValue().isNotApplicable());

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(savedAnswerId, saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(QUESTION_ID, saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(HistoryType.PERSIST, saveAnswerHistoryParam.getValue().getHistoryType());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerOptionIdChangedForExistsAnswer_UpdatesAnswerAndInvalidatesAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = UUID.randomUUID();
        var isNotApplicable = Boolean.FALSE;
        var newAnswerOptionId = AnswerOptionMother.optionFour().getId();
        var oldAnswerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(newAnswerOptionId)
            .isNotApplicable(isNotApplicable));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var result = service.submitAnswer(param);

        assertInstanceOf(SubmitAnswerUseCase.Submitted.class, result);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(newAnswerOptionId, updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(existAnswer.getId(), saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(QUESTION_ID, saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNotNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(newAnswerOptionId, saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption().getId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(isNotApplicable, saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(HistoryType.UPDATE, saveAnswerHistoryParam.getValue().getHistoryType());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_NotApplicableAnswerSubmittedForExistsApplicableAnswer_UpdateAndInvalidatesAssessmentResult() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.validResult();
        var isNotApplicable = Boolean.TRUE;
        var oldAnswerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(oldAnswerOption.getId())
            .confidenceLevelId(existAnswer.getConfidenceLevelId())
            .isNotApplicable(isNotApplicable)
            .currentUserId(currentUserId));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, ConfidenceLevel.getDefault().getId(), isNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_ConfidenceIdChangedForExistsNotApplicableAnswer_UpdateAndInvalidatesAssessmentResult() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.validResult();
        var isNotApplicable = Boolean.TRUE;
        var oldAnswerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();
        var confidenceLevel = ConfidenceLevel.getMaxLevel();
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(oldAnswerOption.getId())
            .confidenceLevelId(confidenceLevel.getId())
            .isNotApplicable(isNotApplicable)
            .currentUserId(currentUserId));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, confidenceLevel.getId(), isNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertNull(updateAnswerParam.getValue().answerOptionId());
        assertEquals(confidenceLevel.getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOptionSubmittedForExistsAnswer_DoNotInvalidateAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = UUID.randomUUID();
        var sameAnswerOption = AnswerOptionMother.optionFour();
        var existAnswer = AnswerMother.answerWithNullNotApplicable(sameAnswerOption);
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(sameAnswerOption.getId())
            .confidenceLevelId(existAnswer.getConfidenceLevelId())
            .isNotApplicable(null));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);
        var notModifiedResult = (SubmitAnswerUseCase.NotAffected) result;
        assertEquals(existAnswer.getId(), notModifiedResult.id());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort);
    }

    @Test
    void testSubmitAnswer_NotApplicableOfAnswerChangedForExistsAnswer_UpdatesAnswerAndInvalidatesAssessmentResult() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var savedAnswerHistoryId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.validResult();
        var newIsNotApplicable = Boolean.FALSE;
        var answerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableTrue(answerOption);
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(answerOption.getId())
            .confidenceLevelId(ConfidenceLevel.getDefault().getId())
            .isNotApplicable(newIsNotApplicable)
            .currentUserId(currentUserId));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), answerOption.getId(), ConfidenceLevel.getDefault().getId(), newIsNotApplicable, currentUserId);
        doNothing().when(updateAnswerPort).update(updateParam);

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(newIsNotApplicable, updateAnswerParam.getValue().isNotApplicable());
        assertEquals(currentUserId, updateAnswerParam.getValue().currentUserId());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_NotApplicableAnswerSubmittedForApplicableQuestion_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.validResult();
        var newIsNotApplicable = Boolean.TRUE;
        var answerOption = AnswerOptionMother.optionOne();
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(answerOption.getId())
            .confidenceLevelId(ConfidenceLevel.getDefault().getId())
            .isNotApplicable(newIsNotApplicable)
            .currentUserId(currentUserId));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> service.submitAnswer(param));
        assertEquals(SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE, exception.getMessageKey());

        verifyNoInteractions(createAnswerPort,
            createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            loadAnswerPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_SameIsNotApplicableAnswerSubmittedForExistsAnswer_DoNotInvalidateAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = UUID.randomUUID();
        var existAnswer = AnswerMother.answerWithNotApplicableTrue(null);
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(null)
            .confidenceLevelId(existAnswer.getConfidenceLevelId())
            .isNotApplicable(Boolean.TRUE));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);
        var notModifiedResult = (SubmitAnswerUseCase.NotAffected) result;
        assertEquals(existAnswer.getId(), notModifiedResult.id());

        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(any());
        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verifyNoInteractions(createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_ConfidenceIdChangedForExistsApplicableAnswer_UpdateAndInvalidatesAssessmentResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = UUID.randomUUID();
        var isNotApplicable = Boolean.FALSE;
        var newConfidenceLevelId = 3;
        var answerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(answerOption);
        var param = createParam(b -> b
            .assessmentId(assessmentId)
            .answerOptionId(answerOption.getId())
            .confidenceLevelId(newConfidenceLevelId)
            .isNotApplicable(isNotApplicable));

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), QUESTION_ID)).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(assessmentId, submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParam = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParam.capture());
        assertEquals(existAnswer.getId(), updateAnswerParam.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(newConfidenceLevelId, updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(isNotApplicable, updateAnswerParam.getValue().isNotApplicable());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), QUESTION_ID);
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultCalculatePort);
    }

    private SubmitAnswerUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(QUESTIONNAIRE_ID)
            .questionId(QUESTION_ID)
            .answerOptionId(18663L)
            .confidenceLevelId(ConfidenceLevel.getDefault().getId())
            .isNotApplicable(Boolean.FALSE)
            .currentUserId(UUID.randomUUID());
    }
}
