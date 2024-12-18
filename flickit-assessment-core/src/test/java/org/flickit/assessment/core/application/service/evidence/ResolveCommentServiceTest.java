package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidence.ResolveCommentUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidence.ResolveCommentPort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.RESOLVE_COMMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_USER_ROLE_ID_NOT_FOUND;
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

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Test
    void testResolveComment_whenCurrentUserDoesntHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(ResolveCommentUseCase.Param.ParamBuilder::build);
        Evidence evidence = EvidenceMother.simpleEvidence();

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.resolveComment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(resolveCommentPort, loadUserRoleForAssessmentPort);
    }

    @Test
    void testResolveComment_whenAssessmentUserRoleDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(ResolveCommentUseCase.Param.ParamBuilder::build);
        Evidence evidence = EvidenceMother.simpleEvidence();

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.resolveComment(param));
        assertEquals(ASSESSMENT_USER_ROLE_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveComment_whenUserWithAssociateRoleResolvedOtherUsersComment_thenThrowAccessDeniedException() {
        var param = createParam(ResolveCommentUseCase.Param.ParamBuilder::build);
        Evidence evidence = EvidenceMother.simpleEvidence();

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.ASSOCIATE));

        var throwable = assertThrows(AccessDeniedException.class, () -> service.resolveComment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveComment_whenEvidenceHasPositiveOrNegativeType_thenThrowValidationException() {
        Evidence evidence = EvidenceMother.simpleEvidence();
        var param = createParam(b -> b.currentUserId(evidence.getCreatedById()));

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.ASSOCIATE));

        var throwable = assertThrows(ValidationException.class, () -> service.resolveComment(param));
        assertEquals(RESOLVE_COMMENT_INCORRECT_EVIDENCE_TYPE, throwable.getMessageKey());

        verifyNoInteractions(resolveCommentPort);
    }

    @Test
    void testResolveComment_whenUserWithAssociateRoleTriedToResolveTheirComment_thenResolveComment() {
        Evidence evidence = EvidenceMother.evidenceAsComment();
        var param = createParam(b -> b.currentUserId(evidence.getCreatedById()));

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.ASSOCIATE));
        doNothing().when(resolveCommentPort).resolveComment(evidence);

        service.resolveComment(param);

        verify(resolveCommentPort).resolveComment(evidence);
    }

    @Test
    void testResolveComment_whenUserWithAssessorRoleTriedToResolveComment_thenResolveComment() {
        Evidence evidence = EvidenceMother.evidenceAsComment();
        var param = createParam(b -> b.currentUserId(evidence.getCreatedById()));

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.ASSESSOR));
        doNothing().when(resolveCommentPort).resolveComment(evidence);

        service.resolveComment(param);

        verify(resolveCommentPort).resolveComment(evidence);
    }

    @Test
    void testResolveComment_whenUserWithManagerRoleTriedToResolveComment_thenResolveComment() {
        Evidence evidence = EvidenceMother.evidenceAsComment();
        var param = createParam(b -> b.currentUserId(evidence.getCreatedById()));

        when(loadEvidencePort.loadNotDeletedEvidence(param.getId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), RESOLVE_COMMENT)).thenReturn(true);
        when(loadUserRoleForAssessmentPort.load(evidence.getAssessmentId(), param.getCurrentUserId())).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        doNothing().when(resolveCommentPort).resolveComment(evidence);

        service.resolveComment(param);

        verify(resolveCommentPort).resolveComment(evidence);
    }

    private ResolveCommentUseCase.Param createParam(Consumer<ResolveCommentUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private ResolveCommentUseCase.Param.ParamBuilder paramBuilder() {
        return ResolveCommentUseCase.Param.builder()
            .id(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
