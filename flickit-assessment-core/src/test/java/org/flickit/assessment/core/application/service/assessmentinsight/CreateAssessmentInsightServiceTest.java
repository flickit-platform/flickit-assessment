package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase.*;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentInsightServiceTest {

    @InjectMocks
    private CreateAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Mock
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Test
    void testCreateAssessmentInsight_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, "assessment insight", currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT);
    }

    @Test
    void testCreateAssessmentInsight_NoAssessmentResult_ThrowResourceNotFoundException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, "assessment insight", currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAssessmentInsight(param));
        assertEquals(CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
    }

    @Test
    void testCreateAssessmentInsight_NoAssessmentInsightFound_CreateAssessmentInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, "assessment insight", currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessmentInsightId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightPort.persist(any(AssessmentInsight.class))).thenReturn(assessmentInsightId);

        var result = assertDoesNotThrow(() -> service.createAssessmentInsight(param));

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
        verify(loadAssessmentInsightPort).loadByAssessmentResultId(assessmentResult.getId());

        assertEquals(assessmentInsightId, result.id());
    }

    @Test
    void testCreateAssessmentInsight_AssessmentInsightFound_UpdateAssessmentInsight() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(assessmentId, "assessment insight", currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessmentInsight = AssessmentInsightMother.createWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        doNothing().when(updateAssessmentInsightPort).updateInsight(any(AssessmentInsight.class));

        var result = assertDoesNotThrow(() -> service.createAssessmentInsight(param));

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT);
        verify(loadAssessmentResultPort).loadByAssessmentId(assessmentId);
        verify(updateAssessmentInsightPort).updateInsight(isA(AssessmentInsight.class));

        assertEquals(assessmentInsight.getId(), result.id());
    }
}
