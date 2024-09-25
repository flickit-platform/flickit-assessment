package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentServiceTest {

    @InjectMocks
    private GetAssessmentService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Test
    @DisplayName("The GetAssessment service should successfully return the result when the currentUser has Manager role")
    void testGetAssessment_validResultManageableViewable_successful() {
        var maturityLevel = MaturityLevelMother.levelThree();
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, maturityLevel);
        Assessment assessment = assessmentResult.getAssessment();
        UUID assessmentId = assessment.getId();
        User assessmentCreator = new User(assessment.getCreatedBy(), "Display name", "user@mail.com");
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadById(assessment.getCreatedBy())).thenReturn(Optional.of(assessmentCreator));
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentId, currentUserId)).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        when(assessmentPermissionChecker.isAuthorized(eq(assessmentId), eq(currentUserId), any())).thenReturn(true);

        Result result = service.getAssessment(new Param(assessmentId, currentUserId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        assertEquals(assessment.getTitle(), result.title());
        assertEquals(assessment.getSpace().getId(), result.space().getId());
        assertEquals(assessment.getSpace().getTitle(), result.space().getTitle());
        assertEquals(assessment.getAssessmentKit().getId(), result.kit().getId());
        assertEquals(assessment.getAssessmentKit().getTitle(), result.kit().getTitle());
        assertEquals(assessment.getCreationTime(), result.creationTime());
        assertEquals(assessment.getLastModificationTime(), result.lastModificationTime());
        assertEquals(assessmentCreator.getId(), result.createdBy().getId());
        assertEquals(assessmentCreator.getDisplayName(), result.createdBy().getDisplayName());
        assertEquals(maturityLevel, result.maturityLevel());
        assertEquals(assessmentResult.getIsCalculateValid(), result.isCalculateValid());
        assertTrue(result.manageable());
        assertTrue(result.viewable());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
        verify(loadUserPort, times(1)).loadById(any());
    }

    @Test
    @DisplayName("The GetAssessment service should successfully return the result when currentUser has not Manager role but has view permission.")
    void testGetAssessment_validResultNotManageableViewable_successful() {
        var maturityLevel = MaturityLevelMother.levelThree();
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, maturityLevel);
        Assessment assessment = assessmentResult.getAssessment();
        UUID assessmentId = assessment.getId();
        User assessmentCreator = new User(assessment.getCreatedBy(), "Display name", "user@mail.com");
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadById(assessment.getCreatedBy())).thenReturn(Optional.of(assessmentCreator));
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentId, currentUserId)).thenReturn(Optional.of(AssessmentUserRole.ASSESSOR));
        when(assessmentPermissionChecker.isAuthorized(eq(assessmentId), eq(currentUserId), any())).thenReturn(true);

        Result result = service.getAssessment(new Param(assessmentId, currentUserId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertFalse(result.manageable());
        assertTrue(result.viewable());
        assertNotNull(result.maturityLevel());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
        verify(loadUserPort, times(1)).loadById(any());
    }

    @Test
    @DisplayName("The GetAssessment service should successfully return the result when the currentUser doesn't have view permission")
    void testGetAssessment_validResultNotManageableNotViewable_successful() {
        var maturityLevel = MaturityLevelMother.levelThree();
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, maturityLevel);
        Assessment assessment = assessmentResult.getAssessment();
        UUID assessmentId = assessment.getId();
        User assessmentCreator = new User(assessment.getCreatedBy(), "Display name", "user@mail.com");
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.of(assessment));
        when(loadUserPort.loadById(assessment.getCreatedBy())).thenReturn(Optional.of(assessmentCreator));
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(assessmentPermissionChecker.isAuthorized (eq(assessmentId), eq(currentUserId),any())).thenReturn(false);

        Result result = service.getAssessment(new Param(assessmentId, currentUserId));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertFalse(result.manageable());
        assertFalse(result.viewable());
        assertNull(result.maturityLevel());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
        verify(loadUserPort, times(1)).loadById(any());
    }

    @Test
    @DisplayName("The GetAssessment service should throw a ResourceNotFoundException when the assessment ID is invalid.")
    void getAssessment_invalidAssessmentId_ResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.empty());

        Param param = new Param(assessmentId, currentUserId);
        assertThrows(ResourceNotFoundException.class, () -> service.getAssessment(param));

        ArgumentCaptor<UUID> assessmentIdArgument = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentPort).getAssessmentById(assessmentIdArgument.capture());

        assertEquals(assessmentId, assessmentIdArgument.getValue());
        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, times(1)).getAssessmentById(any());
        verify(loadUserPort, never()).loadById(any());
        verifyNoInteractions(loadUserRoleForAssessmentPort, assessmentPermissionChecker);
    }

    @Test
    @DisplayName("The GetAssessment service should throw an AccessDeniedException when the user does not have access.")
    void getAssessment_UserHasNotAccess_AccessDeniedException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT)).thenReturn(false);

        Param param = new Param(assessmentId, currentUserId);
        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker, times(1)).isAuthorized(any(), any(), any());
        verify(getAssessmentPort, never()).getAssessmentById(any());
        verifyNoInteractions(loadUserRoleForAssessmentPort, assessmentPermissionChecker);
    }
}
