package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentInsightServiceTest {

    @InjectMocks
    private CreateAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testCreateAssessmentInsight_CurrentUserHasNotAccess_ShouldReturnAccessDeniedException() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new CreateAssessmentInsightUseCase.Param(assessmentId, "assessment insight", currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(assessmentId, currentUserId, AssessmentPermission.CREATE_ASSESSMENT_INSIGHT);
    }
}
