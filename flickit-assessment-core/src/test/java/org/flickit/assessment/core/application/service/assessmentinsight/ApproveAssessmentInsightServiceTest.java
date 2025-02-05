package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentinsight.ApproveAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinsight.ApproveAssessmentInsightPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ASSIGNMENT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveAssessmentInsightServiceTest {

    @InjectMocks
    private ApproveAssessmentInsightService service;

    @Mock
    private ApproveAssessmentInsightPort approveAssessmentInsightPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testApproveAssessmentInsight_whenCurrentUserDoesNotHaveRequiredPermissions_thenThrowAccessDeniedException() {
        var param = createParam(ApproveAssessmentInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ASSIGNMENT_INSIGHT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(approveAssessmentInsightPort);
    }

    @Test
    void testApproveAssessmentInsight_whenCurrentUserHasRequiredPermissions_thenApproveAssessmentInsight() {
        var param = createParam(ApproveAssessmentInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ASSIGNMENT_INSIGHT))
            .thenReturn(true);
        doNothing().when(approveAssessmentInsightPort).approve(eq(param.getAssessmentId()), any(LocalDateTime.class));

        service.approveAssessmentInsight(param);

        verify(approveAssessmentInsightPort).approve(eq(param.getAssessmentId()), any(LocalDateTime.class));
    }

    private ApproveAssessmentInsightUseCase.Param createParam(Consumer<ApproveAssessmentInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private ApproveAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
