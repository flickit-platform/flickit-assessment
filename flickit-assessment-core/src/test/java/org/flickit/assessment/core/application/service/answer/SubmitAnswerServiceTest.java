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
        var param = createParam(Param.ParamBuilder::build);
        var assessmentId = param.getAssessmentId();

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
        var param = createParam(b -> b.isNotApplicable(Boolean.FALSE));
        var assessmentResult = AssessmentResultMother.validResult();
        var savedAnswerId = UUID.randomUUID();
        var savedAnswerHistoryId = UUID.randomUUID();
        var assessmentId = param.getAssessmentId();

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
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
        assertEquals(param.getQuestionnaireId(), saveAnswerParam.getValue().questionnaireId());
        assertEquals(param.getQuestionId(), saveAnswerParam.getValue().questionId());
        assertEquals(param.getAnswerOptionId(), saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(param.getIsNotApplicable(), saveAnswerParam.getValue().isNotApplicable());

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(savedAnswerId, saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(param.getQuestionId(), saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNotNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(param.getAnswerOptionId(), saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption().getId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(param.getIsNotApplicable(), saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(HistoryType.PERSIST, saveAnswerHistoryParam.getValue().getHistoryType());

        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_NewApplicableAnswerWithNullAnswerOptionIdSubmitted_DontSavesAnswerAndDontInvalidatesAssessmentResult() {
        var param = createParam(b -> b
            .isNotApplicable(Boolean.FALSE)
            .answerOptionId(null));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = param.getAssessmentId();

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());

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
        var param = createParam(b -> b.isNotApplicable(Boolean.TRUE));
        var assessmentResult = AssessmentResultMother.validResult();
        var savedAnswerId = UUID.randomUUID();
        var savedAnswerHistoryId = UUID.randomUUID();
        var assessmentId = param.getAssessmentId();

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
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
        assertEquals(param.getQuestionnaireId(), saveAnswerParam.getValue().questionnaireId());
        assertEquals(param.getQuestionId(), saveAnswerParam.getValue().questionId());
        assertNull(saveAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerParam.getValue().confidenceLevelId());
        assertEquals(param.getIsNotApplicable(), saveAnswerParam.getValue().isNotApplicable());

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(savedAnswerId, saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(param.getQuestionId(), saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(param.getIsNotApplicable(), saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(HistoryType.PERSIST, saveAnswerHistoryParam.getValue().getHistoryType());

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerOptionIdChangedForExistsAnswer_UpdatesAnswerAndInvalidatesAssessmentResult() {
        var param = createParam(b -> b
            .isNotApplicable(Boolean.FALSE)
            .answerOptionId(AnswerOptionMother.optionFour().getId()));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = param.getAssessmentId();
        var oldAnswerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
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
        assertEquals(param.getAnswerOptionId(), updateAnswerParam.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(param.getIsNotApplicable(), updateAnswerParam.getValue().isNotApplicable());

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(existAnswer.getId(), saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(param.getQuestionId(), saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNotNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(param.getAnswerOptionId(), saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption().getId());
        assertEquals(ConfidenceLevel.getDefault().getId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(param.getIsNotApplicable(), saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(HistoryType.UPDATE, saveAnswerHistoryParam.getValue().getHistoryType());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_NotApplicableAnswerSubmittedForExistsApplicableAnswer_UpdateAndInvalidatesAssessmentResult() {
        var param = createParam(b -> b.isNotApplicable(Boolean.TRUE));
        var assessmentId = param.getAssessmentId();
        var assessmentResult = AssessmentResultMother.validResult();
        var oldAnswerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, ConfidenceLevel.getDefault().getId(), param.getIsNotApplicable(), param.getCurrentUserId());
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
        assertEquals(param.getIsNotApplicable(), updateAnswerParam.getValue().isNotApplicable());
        assertEquals(param.getCurrentUserId(), updateAnswerParam.getValue().currentUserId());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_ConfidenceIdChangedForExistsNotApplicableAnswer_UpdateAndInvalidatesAssessmentResult() {
        var param = createParam(b -> b
            .isNotApplicable(Boolean.TRUE)
            .confidenceLevelId(ConfidenceLevel.getMaxLevel().getId()));
        var assessmentId = param.getAssessmentId();
        var assessmentResult = AssessmentResultMother.validResult();
        var oldAnswerOption = AnswerOptionMother.optionOne();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), null, param.getConfidenceLevelId(), param.getIsNotApplicable(), param.getCurrentUserId());
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
        assertEquals(param.getConfidenceLevelId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(param.getIsNotApplicable(), updateAnswerParam.getValue().isNotApplicable());
        assertEquals(param.getCurrentUserId(), updateAnswerParam.getValue().currentUserId());

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort);
    }

    @Test
    void testSubmitAnswer_AnswerWithSameAnswerOptionSubmittedForExistsAnswer_DoNotInvalidateAssessmentResult() {
        var sameAnswerOption = AnswerOptionMother.optionFour();
        var param = createParam(b -> b.answerOptionId(sameAnswerOption.getId())
            .isNotApplicable(null));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = param.getAssessmentId();
        var existAnswer = AnswerMother.answerWithNullNotApplicable(sameAnswerOption);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);
        var notModifiedResult = (SubmitAnswerUseCase.NotAffected) result;
        assertEquals(existAnswer.getId(), notModifiedResult.id());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort);
    }

    @Test
    void testSubmitAnswer_NotApplicableOfAnswerChangedForExistsAnswer_UpdatesAnswerAndInvalidatesAssessmentResult() {
        var answerOption = AnswerOptionMother.optionOne();
        var param = createParam(b -> b
            .isNotApplicable(Boolean.FALSE)
            .answerOptionId(answerOption.getId()));
        var assessmentId = param.getAssessmentId();
        var savedAnswerHistoryId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.validResult();
        var existAnswer = AnswerMother.answerWithNotApplicableTrue(answerOption);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var updateParam = new UpdateAnswerPort.Param(existAnswer.getId(), answerOption.getId(), ConfidenceLevel.getDefault().getId(), param.getIsNotApplicable(), param.getCurrentUserId());
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
        assertEquals(param.getIsNotApplicable(), updateAnswerParam.getValue().isNotApplicable());
        assertEquals(param.getCurrentUserId(), updateAnswerParam.getValue().currentUserId());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_NotApplicableAnswerSubmittedForApplicableQuestion_ThrowsException() {
        var param = createParam(b -> b.isNotApplicable(Boolean.TRUE));
        var assessmentId = param.getAssessmentId();
        var assessmentResult = AssessmentResultMother.validResult();

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
        var param = createParam(b -> b
            .isNotApplicable(Boolean.TRUE)
            .answerOptionId(null));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = param.getAssessmentId();
        var existAnswer = AnswerMother.answerWithNotApplicableTrue(null);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);
        var notModifiedResult = (SubmitAnswerUseCase.NotAffected) result;
        assertEquals(existAnswer.getId(), notModifiedResult.id());

        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(any());
        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verifyNoInteractions(createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_ConfidenceIdChangedForExistsApplicableAnswer_UpdateAndInvalidatesAssessmentResult() {
        var answerOption = AnswerOptionMother.optionOne();
        var param = createParam(b -> b
            .isNotApplicable(Boolean.FALSE)
            .confidenceLevelId(3)
            .answerOptionId(answerOption.getId()));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentId = param.getAssessmentId();
        var existAnswer = AnswerMother.answerWithNotApplicableFalse(answerOption);

        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));

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
        assertEquals(param.getConfidenceLevelId(), updateAnswerParam.getValue().confidenceLevelId());
        assertEquals(param.getIsNotApplicable(), updateAnswerParam.getValue().isNotApplicable());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultCalculatePort);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(25L)
            .questionId(1L)
            .answerOptionId(18663L)
            .confidenceLevelId(ConfidenceLevel.getDefault().getId())
            .isNotApplicable(Boolean.FALSE)
            .currentUserId(UUID.randomUUID());
    }
}
