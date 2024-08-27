package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase.*;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentInsightServiceTest {

    @InjectMocks
    private GetAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    ValidateAssessmentResult validateAssessmentResult;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Test
    void testGetAssessmentInsight_UserWithoutAccess_ShouldReturnAccessDenied() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentInsight(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT);
        verifyNoInteractions(loadAssessmentResultPort, validateAssessmentResult, loadAssessmentInsightPort);
    }

    @Test
    void testGetAssessmentInsight_AssessmentResultNotFound_ShouldThrowResourceNotFound() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentInsight(param));

        assertEquals(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
        verifyNoInteractions(validateAssessmentResult, loadAssessmentInsightPort);
    }

    @Test
    void testGetAssessmentInsight_AssessmentInsightFound_ShouldReturnAssessmentInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessmentInsight = AssessmentInsightMother.createWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));

        var result = assertDoesNotThrow(() -> service.getAssessmentInsight(param));
        assertNotNull(result.assessorInsight());
        assertNull(result.defaultInsight());

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
        verify(validateAssessmentResult).validate(param.getAssessmentId());
        verify(loadAssessmentInsightPort).loadByAssessmentResultId(assessmentResult.getId());
    }

    @Test
    void testGetAssessmentInsight_AssessmentInsightNotFoundAllQuestionAnswered_ShouldReturnDefaultInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, MaturityLevelMother.levelFive());
        var getAssessmentPortResult = new GetAssessmentProgressPort.Result(assessmentId, 10, 10);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(getAssessmentPortResult);

        var result = assertDoesNotThrow(() -> service.getAssessmentInsight(param));
        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight().insight());
        assertTrue(result.defaultInsight().insight().contains("all 10 questions"));

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
        verify(validateAssessmentResult).validate(param.getAssessmentId());
        verify(loadAssessmentInsightPort).loadByAssessmentResultId(assessmentResult.getId());
        verify(getAssessmentProgressPort).getProgress(assessmentResult.getAssessment().getId());
    }

    @Test
    void testGetAssessmentInsight_AssessmentInsightNotFoundPartialQuestionsAnswered_ShouldReturnDefaultInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, MaturityLevelMother.levelFive());
        var getAssessmentPortResult = new GetAssessmentProgressPort.Result(assessmentId, 8, 10);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(getAssessmentPortResult);

        var result = assertDoesNotThrow(() -> service.getAssessmentInsight(param));
        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight().insight());
        assertTrue(result.defaultInsight().insight().contains("8 out of 10"));

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
        verify(validateAssessmentResult).validate(param.getAssessmentId());
        verify(loadAssessmentInsightPort).loadByAssessmentResultId(assessmentResult.getId());
        verify(getAssessmentProgressPort).getProgress(assessmentResult.getAssessment().getId());
    }
}
