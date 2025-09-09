package org.flickit.assessment.core.application.service.advicenarration;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.advicenarration.ApproveAdviceNarrationUseCase;
import org.flickit.assessment.core.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ADVICE_NARRATION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveAdviceNarrationServiceTest {

    @InjectMocks
    private ApproveAdviceNarrationService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private UpdateAdviceNarrationPort updateAdviceNarrationPort;

    private final ApproveAdviceNarrationUseCase.Param param = createParam(ApproveAdviceNarrationUseCase.Param.ParamBuilder::build);

    @Test
    void testApproveAdviceNarration_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ADVICE_NARRATION))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approve(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testApproveAdviceNarration_whenParamsAreValid_thenSuccessfulApprove() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ADVICE_NARRATION))
            .thenReturn(true);

        service.approve(param);

        verify(updateAdviceNarrationPort).approve(eq(param.getAssessmentId()), any());
    }

    private ApproveAdviceNarrationUseCase.Param createParam(Consumer<ApproveAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ApproveAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
