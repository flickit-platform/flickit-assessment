package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.subjectinsight.ApproveSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.subjectinsight.ApproveSubjectInsightPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveSubjectInsightServiceTest {

    @InjectMocks
    private ApproveSubjectInsightService service;

    @Mock
    private ApproveSubjectInsightPort approveSubjectInsightPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testApproveSubjectInsight_whenCurrentUserDoesNotHaveRequiredPermissions_thenThrowAccessDeniedException() {
        var param = createParam(ApproveSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_SUBJECT_INSIGHT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(approveSubjectInsightPort);
    }

    @Test
    void testApproveSubjectInsight_whenCurrentUserHasRequiredPermission_thenApproveSubjectInsight() {
        var param = createParam(ApproveSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_SUBJECT_INSIGHT))
            .thenReturn(true);
        doNothing()
            .when(approveSubjectInsightPort).approve(eq(param.getAssessmentId()), eq(param.getSubjectId()), any(LocalDateTime.class));

        service.approveSubjectInsight(param);

        verify(approveSubjectInsightPort).approve(eq(param.getAssessmentId()), eq(param.getSubjectId()), any(LocalDateTime.class));
    }

    private ApproveSubjectInsightUseCase.Param createParam(Consumer<ApproveSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private ApproveSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
