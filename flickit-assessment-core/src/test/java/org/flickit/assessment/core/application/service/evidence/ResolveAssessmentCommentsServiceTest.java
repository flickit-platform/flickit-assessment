package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_ALL_COMMENTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveAssessmentCommentsServiceTest {

    @InjectMocks
    private ResolveAssessmentCommentsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    private final ResolveAssessmentCommentsUseCase.Param param = createParam(ResolveAssessmentCommentsUseCase.Param.ParamBuilder::build);

    @Test
    void testResolveAssessmentComments_WhenCurrentUserDoesNotHaveRequiredPermission_ThenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), RESOLVE_ALL_COMMENTS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.resolveAllComments(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private ResolveAssessmentCommentsUseCase.Param createParam(Consumer<ResolveAssessmentCommentsUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private ResolveAssessmentCommentsUseCase.Param.ParamBuilder paramBuilder() {
        return ResolveAssessmentCommentsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}
