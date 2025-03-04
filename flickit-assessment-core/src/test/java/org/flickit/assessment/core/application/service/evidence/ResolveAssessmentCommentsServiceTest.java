package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase;
import org.flickit.assessment.core.application.port.out.evidence.ResolveCommentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_ALL_COMMENTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveAssessmentCommentsServiceTest {

    @InjectMocks
    private ResolveAssessmentCommentsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ResolveCommentPort resolveCommentPort;

    private final ResolveAssessmentCommentsUseCase.Param param = createParam(ResolveAssessmentCommentsUseCase.Param.ParamBuilder::build);

    @Test
    void testResolveAssessmentComments_WhenCurrentUserDoesNotHaveRequiredPermission_ThenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), RESOLVE_ALL_COMMENTS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.resolveAllComments(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveAssessmentComments_whenParametersAreValid_thenResolveAllComments() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), RESOLVE_ALL_COMMENTS))
            .thenReturn(true);

        ArgumentCaptor<UUID> assessmentIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> lastModifiedByCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<LocalDateTime> lastModificationTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        service.resolveAllComments(param);

        verify(resolveCommentPort).resolveAllComments(assessmentIdCaptor.capture(), lastModifiedByCaptor.capture(), lastModificationTimeCaptor.capture());
        assertThat(assessmentIdCaptor.getValue()).isEqualTo(param.getAssessmentId());
        assertThat(lastModifiedByCaptor.getValue()).isEqualTo(param.getCurrentUserId());
        assertThat(lastModificationTimeCaptor.getValue()).isNotNull();
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
