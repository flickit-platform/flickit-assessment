package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidence.ResolveCommentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.ResolveCommentPort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_COMMENT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_OWN_COMMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveCommentServiceTest {

    @InjectMocks
    private ResolveCommentService service;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private ResolveCommentPort resolveCommentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    private final Evidence evidence = EvidenceMother.evidenceAsComment();

    @Test
    void testResolveComment_whenCurrentUserIsNotCommenterAndDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(ResolveCommentUseCase.Param.ParamBuilder::build);

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.resolveComment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveComment_whenCurrentUserCommenterAndDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(b -> b.currentUserId(evidence.getCreatedById()));

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_OWN_COMMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.resolveComment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveComment_whenEvidenceHasPositiveOrNegativeType_thenThrowValidationException() {
        Evidence nonCommentEevidence = EvidenceMother.simpleEvidence();
        var param = createParam(ResolveCommentUseCase.Param.ParamBuilder::build);

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(nonCommentEevidence);
        when(assessmentAccessChecker.isAuthorized(nonCommentEevidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, () -> service.resolveComment(param));
        assertEquals(RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE, throwable.getMessageKey());

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveComment_whenUserIsNotCommenterAndHasRequiredPermission_thenResolveComment() {
        var param = createParam(ResolveCommentUseCase.Param.ParamBuilder::build);

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        doNothing().when(resolveCommentPort).resolveComment(any(UUID.class), any(UUID.class), any(LocalDateTime.class));

        service.resolveComment(param);

        verifyResolveCommentPortParams(param.getId(), param.getCurrentUserId());
    }

    @Test
    void testResolveComment_whenUserIsCommenterAndHasRequiredPermission_thenResolveComment() {
        var param = createParam(b -> b.currentUserId(evidence.getCreatedById()));

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_OWN_COMMENT)).thenReturn(true);
        doNothing().when(resolveCommentPort).resolveComment(any(UUID.class), any(UUID.class), any(LocalDateTime.class));

        service.resolveComment(param);

        verifyResolveCommentPortParams(param.getId(), param.getCurrentUserId());
    }

    private void verifyResolveCommentPortParams(UUID commentId, UUID currentUserId) {
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(resolveCommentPort).resolveComment(eq(commentId), eq(currentUserId), timeCaptor.capture());
        assertNotNull(timeCaptor.getValue());
        assertTrue(Duration.between(timeCaptor.getValue(), LocalDateTime.now()).getSeconds() < 1,
            "lastModificationTime should be close to the current time");
    }

    private ResolveCommentUseCase.Param createParam(Consumer<ResolveCommentUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private ResolveCommentUseCase.Param.ParamBuilder paramBuilder() {
        return ResolveCommentUseCase.Param.builder()
            .id(evidence.getId())
            .currentUserId(UUID.randomUUID());
    }
}
