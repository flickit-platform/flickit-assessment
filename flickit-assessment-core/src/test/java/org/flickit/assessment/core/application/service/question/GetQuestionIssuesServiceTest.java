package org.flickit.assessment.core.application.service.question;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.question.GetQuestionIssuesUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_QUESTION_ISSUES_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetQuestionIssuesServiceTest {

    @InjectMocks
    private GetQuestionIssuesService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort assessmentResultPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testGetQuestionIssues_UserDoesNotHaveEnoughAccess_ThrowsAccessDeniedException() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionIssues(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetQuestionIssues_AssessmentResultDoesNotExist_ThrowsResourceNotFoundException() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionIssues(param));
        assertEquals(GET_QUESTION_ISSUES_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetQuestionIssues_AnswerDoesNotExist_successfulWithIssues() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());

        var result = service.getQuestionIssues(param);
        assertTrue(result.isUnanswered());
        assertFalse(result.isAnsweredWithLowConfidence());
        assertFalse(result.isAnsweredWithoutEvidences());
        assertEquals(0, result.unresolvedCommentsCount());
    }

    @Test
    void testGetQuestionIssues_SelectedAnswerOptionIsNull_successfulWithIssues() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
            .thenReturn(Optional.of(AnswerMother.answerWithNotApplicableFalse(null)));

        var result = service.getQuestionIssues(param);
        assertTrue(result.isUnanswered());
        assertFalse(result.isAnsweredWithLowConfidence());
        assertFalse(result.isAnsweredWithoutEvidences());
        assertEquals(0, result.unresolvedCommentsCount());
    }

    @Test
    void testGetQuestionIssues_SelectedAnswerOptionHasLowConfidence_successfulWithIssues() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);
        var answer = AnswerMother.answerWithConfidenceLevel(2, param.getQuestionId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
            .thenReturn(Optional.of(answer));
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId()))
            .thenReturn(1);
        when(countEvidencesPort.countAnsweredQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId()))
            .thenReturn(0);

        var result = service.getQuestionIssues(param);
        assertFalse(result.isUnanswered());
        assertTrue(result.isAnsweredWithLowConfidence());
        assertFalse(result.isAnsweredWithoutEvidences());
        assertEquals(0, result.unresolvedCommentsCount());
    }

    @Test
    void testGetQuestionIssues_SelectedAnswerOptionHasLowConfidenceWithEvidences_successfulWithIssues() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);
        var answer = AnswerMother.answerWithConfidenceLevel(2, param.getQuestionId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
            .thenReturn(Optional.of(answer));
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId()))
            .thenReturn(2);
        when(countEvidencesPort.countAnsweredQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId()))
            .thenReturn(0);

        var result = service.getQuestionIssues(param);
        assertFalse(result.isUnanswered());
        assertTrue(result.isAnsweredWithLowConfidence());
        assertFalse(result.isAnsweredWithoutEvidences());
        assertEquals(0, result.unresolvedCommentsCount());
    }

    @Test
    void testGetQuestionIssues_SelectedAnswerOptionWithoutEvidencesAndUnresolvedComments_successfulWithIssues() {
        var param = createParam(GetQuestionIssuesUseCase.Param.ParamBuilder::build);
        var answer = AnswerMother.answerWithConfidenceLevel(3, param.getQuestionId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId()))
            .thenReturn(Optional.of(answer));
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId()))
            .thenReturn(0);
        when(countEvidencesPort.countAnsweredQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId()))
            .thenReturn(2);

        var result = service.getQuestionIssues(param);
        assertFalse(result.isUnanswered());
        assertFalse(result.isAnsweredWithLowConfidence());
        assertTrue(result.isAnsweredWithoutEvidences());
        assertEquals(2, result.unresolvedCommentsCount());
    }

    private GetQuestionIssuesUseCase.Param createParam(Consumer<GetQuestionIssuesUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetQuestionIssuesUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionIssuesUseCase.Param.builder()
            .questionId(0L)
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
