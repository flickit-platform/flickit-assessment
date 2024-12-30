package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_INSIGHT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createWithAssessmentResultId;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createWithMinInsightTime;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.*;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelFive;
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

    @Test
    void testGetAssessmentInsight_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
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
    void testGetAssessmentInsight_AssessmentResultNotFound_ThrowResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentInsight(param));
        assertEquals(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResult, loadAssessmentInsightPort);
    }

    @Test
    void testGetAssessmentInsight_AssessmentInsightDoesNotExist_ThrowResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = validResult();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentInsight(param));
        assertEquals(GET_ASSESSMENT_INSIGHT_ASSESSMENT_INSIGHT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetAssessmentInsight_AssessmentInsightExistsAndIsValidAndEditable_ReturnAssessorInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = validResult();
        var assessmentInsight = createWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);

        var result = assertDoesNotThrow(() -> service.getAssessmentInsight(param));

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(assessmentInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(assessmentInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertTrue(result.assessorInsight().isValid());
        assertTrue(result.editable());
    }

    @Test
    void testGetAssessmentInsight_AssessmentInsightExistsAndIsNotValidAndNotEditable_ReturnAssessorInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = validResult();
        var assessmentInsight = createWithMinInsightTime();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);

        var result = assertDoesNotThrow(() -> service.getAssessmentInsight(param));

        assertNotNull(result.assessorInsight());
        assertEquals(assessmentInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(assessmentInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertNull(result.defaultInsight());
        assertFalse(result.editable());
    }

    @Test
    void testGetAssessmentInsight_InitialAssessmentInsightExists_ReturnDefaultInsightWithFullProgress() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, currentUserId);
        var assessmentResult = validResultWithSubjectValuesAndMaturityLevel(null, levelFive());
        var assessmentInsight = AssessmentInsightMother.createInitialInsightWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);

        var result = assertDoesNotThrow(() -> service.getAssessmentInsight(param));

        assertNotNull(result.defaultInsight().insight());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
    }
}
