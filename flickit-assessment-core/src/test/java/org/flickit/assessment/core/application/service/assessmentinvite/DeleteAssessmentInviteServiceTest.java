package org.flickit.assessment.core.application.service.assessmentinvite;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentinvite.DeleteAssessmentInviteUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.DeleteAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInvitePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInviteMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT_INVITE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAssessmentInviteServiceTest {

    @InjectMocks
    private DeleteAssessmentInviteService service;

    @Mock
    private DeleteAssessmentInvitePort deleteAssessmentInvitePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentInvitePort loadAssessmentInvitePort;

    private final DeleteAssessmentInviteUseCase.Param param = createParam(DeleteAssessmentInviteUseCase.Param.ParamBuilder::build);

    @Test
    void testDeleteAssessmentInvite_whenCurrentUserDoesNotHaveRequiredPermissionAndUserRoleIsNotReportViewer_thenThrowException() {
        var assessmentInvite = AssessmentInviteMother.assessmentInviteWithRole(AssessmentUserRole.ASSESSOR, param.getCurrentUserId());

        when(loadAssessmentInvitePort.load(param.getId())).thenReturn(assessmentInvite);
        when(assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), param.getCurrentUserId(), DELETE_ASSESSMENT_INVITE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteInvite(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteAssessmentInvitePort);
    }

    @Test
    void testDeleteAssessmentInvite_whenUserDoesNotHaveRequiredPermissionAndIsNotUseInviter_thenThrowException() {
        var assessmentInvite = AssessmentInviteMother.assessmentInviteWithRole(AssessmentUserRole.REPORT_VIEWER, UUID.randomUUID());

        when(loadAssessmentInvitePort.load(param.getId())).thenReturn(assessmentInvite);
        when(assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), param.getCurrentUserId(), DELETE_ASSESSMENT_INVITE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteInvite(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteAssessmentInvitePort);
    }


    @Test
    void testDeleteAssessmentInvite_whenParametersAreValid_thenDelete() {
        AssessmentInvite assessmentInvite = AssessmentInviteMother.assessmentInviteWithRole(AssessmentUserRole.COMMENTER, param.getCurrentUserId());
        when(loadAssessmentInvitePort.load(param.getId())).thenReturn(assessmentInvite);
        when(assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), param.getCurrentUserId(), DELETE_ASSESSMENT_INVITE)).thenReturn(true);

        service.deleteInvite(param);

        verify(deleteAssessmentInvitePort, times(1)).delete(param.getId());
    }

    @Test
    void testDeleteAssessmentInvite_whenParametersAreValidWithoutPermissionButInviter_thenDelete() {
        AssessmentInvite assessmentInvite = AssessmentInviteMother.assessmentInviteWithRole(AssessmentUserRole.REPORT_VIEWER, param.getCurrentUserId());
        when(loadAssessmentInvitePort.load(param.getId())).thenReturn(assessmentInvite);
        when(assessmentAccessChecker.isAuthorized(assessmentInvite.getAssessmentId(), param.getCurrentUserId(), DELETE_ASSESSMENT_INVITE)).thenReturn(false);

        service.deleteInvite(param);

        verify(deleteAssessmentInvitePort, times(1)).delete(param.getId());
    }

    private DeleteAssessmentInviteUseCase.Param createParam(Consumer<DeleteAssessmentInviteUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteAssessmentInviteUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAssessmentInviteUseCase.Param.builder()
            .id(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
