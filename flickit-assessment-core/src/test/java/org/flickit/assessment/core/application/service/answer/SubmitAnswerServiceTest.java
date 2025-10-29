package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
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
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ANSWER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.UNAPPROVED;
import static org.flickit.assessment.core.application.domain.HistoryType.PERSIST;
import static org.flickit.assessment.core.application.domain.HistoryType.UPDATE;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answerWithNotApplicableFalse;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionFour;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionOne;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
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

    @Mock
    private LoadAnswerOptionPort loadAnswerOptionPort;

    private final AssessmentResult assessmentResult = validResult();
    private final AnswerOption answerOption = optionOne();
    private Param param = createParam(Param.ParamBuilder::build);

    @Test
    void testSubmitAnswer_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.submitAnswer(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAnswerPort,
            loadAnswerOptionPort,
            createAnswerPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenNewApplicableAnswerWithAnswerOptionIdSubmitted_thenCreateAnswerAndInvalidateAssessmentResult() {
        var savedAnswerId = UUID.randomUUID();
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
        when(loadAnswerOptionPort.load(param.getAnswerOptionId(), assessmentResult.getKitVersionId())).thenReturn(Optional.of(answerOption));
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var result = service.submitAnswer(param);

        assertNotNull(result);
        assertInstanceOf(SubmitAnswerUseCase.Submitted.class, result);
        SubmitAnswerUseCase.Submitted submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(savedAnswerId, submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var createAnswerParamCaptor = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(createAnswerParamCaptor.capture());
        assertEquals(assessmentResult.getId(), createAnswerParamCaptor.getValue().assessmentResultId());
        assertEquals(param.getQuestionnaireId(), createAnswerParamCaptor.getValue().questionnaireId());
        assertEquals(param.getQuestionId(), createAnswerParamCaptor.getValue().questionId());
        assertEquals(param.getAnswerOptionId(), createAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), createAnswerParamCaptor.getValue().confidenceLevelId());
        assertFalse(createAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(APPROVED, createAnswerParamCaptor.getValue().status());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNotNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            savedAnswerId,
            createAnswerHistoryParamCaptor.getValue(),
            APPROVED,
            PERSIST);

        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, updateAnswerPort);
    }

    @Test
    void testSubmitAnswer_whenNewApplicableAnswerWithNullAnswerOptionIdSubmitted_thenDoNotCreateAnswer() {
        param = createParam(b -> b.answerOptionId(null));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());

        var result = service.submitAnswer(param);
        assertInstanceOf(SubmitAnswerUseCase.NotAffected.class, result);
        assertNull(result.id());

        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            loadAnswerOptionPort,
            createAnswerPort,
            createAnswerHistoryPort,
            updateAnswerPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenNewNotApplicableAnswerSubmitted_thenCreateAnswerAndInvalidateAssessmentResult() {
        param = createParam(b -> b.isNotApplicable(Boolean.TRUE));
        var savedAnswerId = UUID.randomUUID();
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(false);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
        when(createAnswerPort.persist(any(CreateAnswerPort.Param.class))).thenReturn(savedAnswerId);
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var result = service.submitAnswer(param);

        assertInstanceOf(SubmitAnswerUseCase.Submitted.class, result);
        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(savedAnswerId, submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var createAnswerParamCaptor = ArgumentCaptor.forClass(CreateAnswerPort.Param.class);
        verify(createAnswerPort).persist(createAnswerParamCaptor.capture());
        assertEquals(assessmentResult.getId(), createAnswerParamCaptor.getValue().assessmentResultId());
        assertEquals(param.getQuestionnaireId(), createAnswerParamCaptor.getValue().questionnaireId());
        assertEquals(param.getQuestionId(), createAnswerParamCaptor.getValue().questionId());
        assertNull(createAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), createAnswerParamCaptor.getValue().confidenceLevelId());
        assertTrue(createAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(UNAPPROVED, createAnswerParamCaptor.getValue().status());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            savedAnswerId,
            createAnswerHistoryParamCaptor.getValue(),
            UNAPPROVED,
            PERSIST);

        verify(createAnswerPort, times(1)).persist(any(CreateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(updateAnswerPort, loadAnswerOptionPort);
    }

    @Test
    void testSubmitAnswer_whenAnswerOptionChangedForExistsAnswer_thenUpdateAnswerAndInvalidatesAssessmentResult() {
        var oldAnswerOption = optionFour();
        var newAnswerOption = answerOption;
        param = createParam(b -> b.answerOptionId(newAnswerOption.getId()));
        var existAnswer = answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(loadAnswerOptionPort.load(param.getAnswerOptionId(), assessmentResult.getKitVersionId())).thenReturn(Optional.of(newAnswerOption));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        var result = service.submitAnswer(param);

        assertInstanceOf(SubmitAnswerUseCase.Submitted.class, result);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParamCaptor = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParamCaptor.capture());
        assertEquals(existAnswer.getId(), updateAnswerParamCaptor.getValue().answerId());
        assertEquals(param.getAnswerOptionId(), updateAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParamCaptor.getValue().confidenceLevelId());
        assertFalse(updateAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(APPROVED, updateAnswerParamCaptor.getValue().status());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNotNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            existAnswer.getId(),
            createAnswerHistoryParamCaptor.getValue(),
            APPROVED,
            UPDATE);

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenNotApplicableAnswerSubmittedForExistsApplicableAnswer_thenUpdateAnswerAndInvalidateAssessmentResult() {
        var oldAnswerOption = optionOne();
        var existAnswer = answerWithNotApplicableFalse(oldAnswerOption);
        param = createParam(b -> b
            .answerOptionId(oldAnswerOption.getId())
            .confidenceLevelId(existAnswer.getConfidenceLevelId())
            .isNotApplicable(Boolean.TRUE));
        var savedAnswerHistoryId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(false);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        doNothing().when(updateAnswerPort).update(any(UpdateAnswerPort.Param.class));

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParamCaptor = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParamCaptor.capture());
        assertEquals(existAnswer.getId(), updateAnswerParamCaptor.getValue().answerId());
        assertNull(updateAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParamCaptor.getValue().confidenceLevelId());
        assertTrue(updateAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(param.getCurrentUserId(), updateAnswerParamCaptor.getValue().currentUserId());
        assertEquals(UNAPPROVED, updateAnswerParamCaptor.getValue().status());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            existAnswer.getId(),
            createAnswerHistoryParamCaptor.getValue(),
            UNAPPROVED,
            UPDATE);

        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort, invalidateAssessmentResultConfidencePort, loadAnswerOptionPort);
    }

    @Test
    void testSubmitAnswer_whenConfidenceIdChangedForExistsNotApplicableAnswer_thenUpdateAnswerAndInvalidateAssessmentResult() {
        var oldAnswerOption = optionOne();
        var existAnswer = answerWithNotApplicableFalse(oldAnswerOption);
        var savedAnswerHistoryId = UUID.randomUUID();
        var confidenceLevel = ConfidenceLevel.getMaxLevel();
        param = createParam(b -> b
            .answerOptionId(oldAnswerOption.getId())
            .confidenceLevelId(confidenceLevel.getId())
            .isNotApplicable(Boolean.TRUE));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(false);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        doNothing().when(updateAnswerPort).update(any(UpdateAnswerPort.Param.class));

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertTrue(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParamCaptor = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParamCaptor.capture());
        assertEquals(existAnswer.getId(), updateAnswerParamCaptor.getValue().answerId());
        assertNull(updateAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(confidenceLevel.getId(), updateAnswerParamCaptor.getValue().confidenceLevelId());
        assertTrue(updateAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(UNAPPROVED, updateAnswerParamCaptor.getValue().status());
        assertEquals(param.getCurrentUserId(), updateAnswerParamCaptor.getValue().currentUserId());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            existAnswer.getId(),
            createAnswerHistoryParamCaptor.getValue(),
            UNAPPROVED,
            UPDATE);

        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(createAnswerPort, loadAnswerOptionPort);
    }

    @Test
    void testSubmitAnswer_whenAnswerWithSameAnswerSubmittedForExistsAnswer_thenDoNotInvalidateAssessmentResult() {
        var sameAnswerOption = optionFour();
        var existAnswer = answerWithNotApplicableFalse(sameAnswerOption);
        param = createParam(b -> b
            .answerOptionId(sameAnswerOption.getId())
            .confidenceLevelId(existAnswer.getConfidenceLevelId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);
        var notModifiedResult = (SubmitAnswerUseCase.NotAffected) result;
        assertEquals(existAnswer.getId(), notModifiedResult.id());

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            createAnswerPort,
            loadAnswerOptionPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenNotApplicableOfAnswerChangedForExistsAnswer_thenUpdateAnswerAndInvalidateAssessmentResult() {
        var savedAnswerHistoryId = UUID.randomUUID();
        var existAnswer = AnswerMother.answerWithNotApplicableTrue(answerOption);
        param = createParam(b -> b.answerOptionId(answerOption.getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(savedAnswerHistoryId);

        doNothing().when(updateAnswerPort).update(any(UpdateAnswerPort.Param.class));

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParamCaptor = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParamCaptor.capture());
        assertEquals(existAnswer.getId(), updateAnswerParamCaptor.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(ConfidenceLevel.getDefault().getId(), updateAnswerParamCaptor.getValue().confidenceLevelId());
        assertFalse(updateAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(APPROVED, updateAnswerParamCaptor.getValue().status());
        assertEquals(param.getCurrentUserId(), updateAnswerParamCaptor.getValue().currentUserId());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNotNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            existAnswer.getId(),
            createAnswerHistoryParamCaptor.getValue(),
            APPROVED,
            UPDATE);

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            createAnswerPort,
            loadAnswerOptionPort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenNotApplicableAnswerSubmittedForApplicableQuestion_thenThrowValidationException() {
        param = createParam(b -> b.isNotApplicable(Boolean.TRUE));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionMayNotBeApplicablePort.loadMayNotBeApplicableById(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(false);

        var exception = assertThrows(ValidationException.class, () -> service.submitAnswer(param));
        assertEquals(SUBMIT_ANSWER_QUESTION_ID_NOT_MAY_NOT_BE_APPLICABLE, exception.getMessageKey());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verifyNoInteractions(createAnswerPort,
            loadAnswerOptionPort,
            updateAnswerPort,
            createAnswerHistoryPort,
            loadAnswerPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenSameIsNotApplicableAnswerSubmittedForExistsAnswer_thenDoNotInvalidateAssessmentResult() {
        var existAnswer = AnswerMother.answerWithNotApplicableTrue(null);
        param = createParam(b -> b
            .answerOptionId(null)
            .confidenceLevelId(existAnswer.getConfidenceLevelId())
            .isNotApplicable(Boolean.TRUE));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
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
            loadAnswerOptionPort,
            createAnswerHistoryPort,
            invalidateAssessmentResultCalculatePort,
            invalidateAssessmentResultConfidencePort);
    }

    @Test
    void testSubmitAnswer_whenConfidenceIdChangedForExistsApplicableAnswer_thenUpdateAnswerAndInvalidateAssessmentResult() {
        var newConfidenceLevelId = 3;
        var existAnswer = answerWithNotApplicableFalse(answerOption);
        param = createParam(b -> b
            .answerOptionId(answerOption.getId())
            .confidenceLevelId(newConfidenceLevelId));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER)).thenReturn(false);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParamCaptor = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParamCaptor.capture());
        assertEquals(existAnswer.getId(), updateAnswerParamCaptor.getValue().answerId());
        assertEquals(answerOption.getId(), updateAnswerParamCaptor.getValue().answerOptionId());
        assertEquals(newConfidenceLevelId, updateAnswerParamCaptor.getValue().confidenceLevelId());
        assertFalse(updateAnswerParamCaptor.getValue().isNotApplicable());
        assertEquals(UNAPPROVED, updateAnswerParamCaptor.getValue().status());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNotNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            param.getConfidenceLevelId(),
            existAnswer.getId(),
            createAnswerHistoryParamCaptor.getValue(),
            UNAPPROVED,
            UPDATE);

        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort,
            loadAnswerOptionPort,
            createAnswerPort,
            invalidateAssessmentResultCalculatePort,
            loadAnswerOptionPort);
    }

    @Test
    void testSubmitAnswer_whenNullAnswerOptionWithApplicableAnswerSubmittedForExistsAnswer_thenUpdateAnswerAndInvalidateAssessmentResult() {
        var existAnswer = answerWithNotApplicableFalse(answerOption);
        param = createParam(b -> b.answerOptionId(null));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), ANSWER_QUESTION)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(any())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(existAnswer));

        var result = service.submitAnswer(param);

        var submittedResult = (SubmitAnswerUseCase.Submitted) result;
        assertEquals(existAnswer.getId(), submittedResult.id());
        assertEquals(param.getAssessmentId(), submittedResult.notificationCmd().assessmentId());
        assertEquals(param.getCurrentUserId(), submittedResult.notificationCmd().assessorId());
        assertFalse(submittedResult.notificationCmd().hasProgressed());

        var updateAnswerParamCaptor = ArgumentCaptor.forClass(UpdateAnswerPort.Param.class);
        verify(updateAnswerPort).update(updateAnswerParamCaptor.capture());
        assertEquals(existAnswer.getId(), updateAnswerParamCaptor.getValue().answerId());
        assertNull(updateAnswerParamCaptor.getValue().answerOptionId());
        assertNull(updateAnswerParamCaptor.getValue().confidenceLevelId());
        assertFalse(updateAnswerParamCaptor.getValue().isNotApplicable());
        assertNull(updateAnswerParamCaptor.getValue().status());

        var createAnswerHistoryParamCaptor = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(createAnswerHistoryParamCaptor.capture());
        assertNull(createAnswerHistoryParamCaptor.getValue().getAnswer().getSelectedOption());
        assertCreateAnswerHistoryPortParam(param,
            assessmentResult,
            null,
            existAnswer.getId(),
            createAnswerHistoryParamCaptor.getValue(),
            null,
            UPDATE);

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(loadAnswerPort, times(1)).load(assessmentResult.getId(), param.getQuestionId());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(createAnswerHistoryPort, times(1)).persist(any(AnswerHistory.class));
        verify(invalidateAssessmentResultConfidencePort, times(1)).invalidateConfidence(assessmentResult.getId());
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(loadQuestionMayNotBeApplicablePort, createAnswerPort, loadAnswerOptionPort);
    }

    private void assertCreateAnswerHistoryPortParam(Param param,
                                                    AssessmentResult assessmentResult,
                                                    Integer confidenceLevelId,
                                                    UUID savedAnswerId,
                                                    AnswerHistory saveAnswerHistoryParam,
                                                    AnswerStatus answerStatus,
                                                    HistoryType historyType) {
        assertEquals(savedAnswerId, saveAnswerHistoryParam.getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getAssessmentResultId());
        assertEquals(param.getQuestionId(), saveAnswerHistoryParam.getAnswer().getQuestionId());
        assertEquals(confidenceLevelId, saveAnswerHistoryParam.getAnswer().getConfidenceLevelId());
        assertEquals(historyType, saveAnswerHistoryParam.getHistoryType());
        if (saveAnswerHistoryParam.getAnswer().getSelectedOption() != null) {
            assertEquals(savedAnswerId, saveAnswerHistoryParam.getAnswer().getId());
            assertEquals(param.getAnswerOptionId(), saveAnswerHistoryParam.getAnswer().getSelectedOption().getId());
            assertEquals(answerOption.getIndex(), saveAnswerHistoryParam.getAnswer().getSelectedOption().getIndex());
            assertEquals(param.getConfidenceLevelId(), saveAnswerHistoryParam.getAnswer().getConfidenceLevelId());
            assertEquals(param.getIsNotApplicable(), saveAnswerHistoryParam.getAnswer().getIsNotApplicable());
            assertEquals(answerStatus, saveAnswerHistoryParam.getAnswer().getAnswerStatus());
        }
    }

    private SubmitAnswerUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(333L)
            .questionId(123L)
            .answerOptionId(18663L)
            .confidenceLevelId(ConfidenceLevel.getDefault().getId())
            .isNotApplicable(Boolean.FALSE)
            .currentUserId(UUID.randomUUID());
    }
}
