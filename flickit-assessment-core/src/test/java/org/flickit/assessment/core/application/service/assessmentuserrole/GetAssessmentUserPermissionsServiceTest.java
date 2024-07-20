package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserPermissionsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentUserPermissionsServiceTest {

    @InjectMocks
    private GetAssessmentUserPermissionsService getAssessmentUserPermissionsService;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Test
    void testGetAssessmentUserPermissions_WhenUserHasNoRole_ThenHasNoPermissions() {

        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(loadUserRoleForAssessmentPort.load(assessmentId, userId)).thenReturn(Optional.empty());
        var param = new GetAssessmentUserPermissionsUseCase.Param(assessmentId, userId);

        Map<String, Boolean> permissions = getAssessmentUserPermissionsService.getAssessmentUserPermissions(param);
        List<String> assessmentPermissions = Arrays.stream(AssessmentPermission.values())
            .map(AssessmentPermission::getCode)
            .toList();

        assertNotNull(permissions);
        permissions.forEach((k, v) -> {
            assertTrue(assessmentPermissions.contains(k));
            assertFalse(v);
        });
    }

    @ParameterizedTest
    @EnumSource(AssessmentUserRole.class)
    void testGetAssessmentUserPermissions_WhenUserHasARole_ThenHasPermissionsOfThatRole(AssessmentUserRole assessmentUserRole) {

        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(loadUserRoleForAssessmentPort.load(assessmentId, userId)).thenReturn(Optional.of(assessmentUserRole));
        var param = new GetAssessmentUserPermissionsUseCase.Param(assessmentId, userId);

        Map<String, Boolean> userPermissions = getAssessmentUserPermissionsService.getAssessmentUserPermissions(param);
        List<String> assessmentPermissions = Arrays.stream(AssessmentPermission.values())
            .map(AssessmentPermission::getCode)
            .toList();

        List<String> rolePermissions = assessmentUserRole.getPermissions().stream()
            .map(AssessmentPermission::getCode)
            .toList();

        assertNotNull(userPermissions);
        userPermissions.forEach((k, v) -> {
            assertTrue(assessmentPermissions.contains(k));
            if (rolePermissions.contains(k))
                assertTrue(v);
            else
                assertFalse(v);
        });
    }
}
