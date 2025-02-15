package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.HistoryType;
import org.flickit.assessment.core.application.port.in.answer.ApproveAnswerUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.ApproveAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ANSWER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ANSWER_QUESTION_NOT_ANSWERED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveAnswerServiceTest {

    @InjectMocks
    private ApproveAnswerService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private ApproveAnswerPort approveAnswerPort;

    @Mock
    private CreateAnswerHistoryPort createAnswerHistoryPort;

    @Test
    void testApproveAnswer_UserHasNotAccess_ThrowsException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
                .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveAnswer(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
                loadAnswerPort,
                approveAnswerPort,
                createAnswerHistoryPort);
    }

    @Test
    void testApproveAnswer_AssessmentIdNotExist_ThrowsException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
                .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.approveAnswer(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAnswerPort, approveAnswerPort, createAnswerHistoryPort);
    }

    @Test
    void testApproveAnswer_ApproveNotExistsAnswer_ThrowsException() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
                .thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
                .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.approveAnswer(param));
        assertEquals(APPROVE_ANSWER_QUESTION_NOT_ANSWERED, throwable.getMessage());

        verifyNoInteractions(approveAnswerPort, createAnswerHistoryPort);
    }

    @Test
    void testApproveAnswer_ApproveNullAnswerOptionAsApplicableAnswer_ThrowsException() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
                .thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
                .thenReturn(Optional.of(AnswerMother.answer(null)));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.approveAnswer(param));
        assertEquals(APPROVE_ANSWER_QUESTION_NOT_ANSWERED, throwable.getMessage());

        verifyNoInteractions(approveAnswerPort, createAnswerHistoryPort);
    }

    @Test
    void testApproveAnswer_ApproveNotApplicableAnswer_UpdateAnswerStatus() {
        var answer = AnswerMother.answerWithNotApplicableTrueAndUnapprovedStatus(null);
        var param = createParam(b -> b.questionId(answer.getQuestionId()));
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
                .thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
                .thenReturn(Optional.of(answer));
        doNothing()
                .when(approveAnswerPort).approve(answer.getId(), param.getCurrentUserId());
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(UUID.randomUUID());

        service.approveAnswer(param);

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(answer.getId(), saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(answer.getQuestionId(), saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(answer.getConfidenceLevelId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(answer.getIsNotApplicable(), saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(APPROVED, saveAnswerHistoryParam.getValue().getAnswer().getAnswerStatus());
        assertEquals(HistoryType.UPDATE, saveAnswerHistoryParam.getValue().getHistoryType());
    }

    @Test
    void testApproveAnswer_ApproveApplicableAnswerWithNotNullAnswerOption_UpdateAnswerStatus() {
        var answer = AnswerMother.answerWithNotApplicableTrueAndUnapprovedStatus(AnswerOptionMother.optionOne());
        var param = createParam(b -> b.questionId(answer.getQuestionId()));
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ANSWER))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
                .thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
                .thenReturn(Optional.of(answer));
        doNothing()
                .when(approveAnswerPort).approve(answer.getId(), param.getCurrentUserId());
        when(createAnswerHistoryPort.persist(any(AnswerHistory.class))).thenReturn(UUID.randomUUID());

        service.approveAnswer(param);

        var saveAnswerHistoryParam = ArgumentCaptor.forClass(AnswerHistory.class);
        verify(createAnswerHistoryPort).persist(saveAnswerHistoryParam.capture());
        assertEquals(answer.getId(), saveAnswerHistoryParam.getValue().getAnswer().getId());
        assertEquals(assessmentResult.getId(), saveAnswerHistoryParam.getValue().getAssessmentResultId());
        assertEquals(answer.getQuestionId(), saveAnswerHistoryParam.getValue().getAnswer().getQuestionId());
        assertNotNull(saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption());
        assertEquals(answer.getSelectedOption().getId(), saveAnswerHistoryParam.getValue().getAnswer().getSelectedOption().getId());
        assertEquals(answer.getConfidenceLevelId(), saveAnswerHistoryParam.getValue().getAnswer().getConfidenceLevelId());
        assertEquals(answer.getIsNotApplicable(), saveAnswerHistoryParam.getValue().getAnswer().getIsNotApplicable());
        assertEquals(APPROVED, saveAnswerHistoryParam.getValue().getAnswer().getAnswerStatus());
        assertEquals(HistoryType.UPDATE, saveAnswerHistoryParam.getValue().getHistoryType());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
                .assessmentId(UUID.randomUUID())
                .questionId(1563L)
                .currentUserId(UUID.randomUUID());
    }
}
