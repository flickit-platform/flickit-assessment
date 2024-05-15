package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.permission.AssessmentPermission;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.core.common.AssessmentUserRole;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentPermissionCheckerServiceTest {

    @InjectMocks
    private AssessmentPermissionCheckerService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Test
    void testIsAuthorized_userIsSpaceOwner_shouldHaveManagerRole() {
        Assessment assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var userId = UUID.randomUUID();
        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.of(assessment));

        when(loadSpaceOwnerPort.loadOwnerId(assessment.getSpaceId()))
            .thenReturn(userId);

        AssessmentUserRole.MANAGER.getPermissions().forEach(x ->
            assertTrue(service.isAuthorized(assessmentId, userId, x))
        );
        verifyNoInteractions(loadUserRoleForAssessmentPort);
    }

    @Test
    void testIsAuthorized_userIsAssessmentCreator_shouldHaveManagerRole() {
        Assessment assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var userId = assessment.getCreatedBy();
        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.of(assessment));

        AssessmentUserRole.MANAGER.getPermissions().forEach(x ->
            assertTrue(service.isAuthorized(assessmentId, userId, x))
        );
        verifyNoInteractions(loadSpaceOwnerPort, loadUserRoleForAssessmentPort);
    }

    @Test
    void testIsAuthorized_userHasNotAnyRole_shouldReturnFalseForAccesses() {
        Assessment assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var userId = UUID.randomUUID();

        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.of(assessment));
        when(loadSpaceOwnerPort.loadOwnerId(assessment.getSpaceId()))
            .thenReturn(UUID.randomUUID());
        when(loadUserRoleForAssessmentPort.load(assessmentId, userId))
            .thenReturn(null);

        AssessmentUserRole.VIEWER.getPermissions().forEach(x ->
            assertFalse(service.isAuthorized(assessmentId, userId, x))
        );
    }

    @Test
    void testIsAuthorized_userHasViewerRole_shouldReturnValidResult() {
        Assessment assessment = AssessmentMother.assessment();
        var assessmentId = assessment.getId();
        var userId = UUID.randomUUID();

        when(getAssessmentPort.getAssessmentById(assessmentId))
            .thenReturn(Optional.of(assessment));
        when(loadSpaceOwnerPort.loadOwnerId(assessment.getSpaceId()))
            .thenReturn(UUID.randomUUID());
        when(loadUserRoleForAssessmentPort.load(assessmentId, userId))
            .thenReturn(AssessmentUserRole.VIEWER);

        AssessmentUserRole.VIEWER.getPermissions().forEach(x ->
            assertTrue(service.isAuthorized(assessmentId, userId, x))
        );

        Set<AssessmentPermission> managerPermissions = new HashSet<>(AssessmentUserRole.MANAGER.getPermissions());
        managerPermissions.removeAll(AssessmentUserRole.ASSESSOR.getPermissions());
        managerPermissions.forEach(x ->
            assertFalse(service.isAuthorized(assessmentId, userId, x))
        );
    }
}
