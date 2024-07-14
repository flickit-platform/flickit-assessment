package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.SpaceAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GrantUserAssessmentRoleUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAssessmentRoleServiceTest {

    @InjectMocks
    private GrantUserAssessmentRoleService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private SpaceAccessChecker spaceAccessChecker;

    @Mock
    private GrantUserAssessmentRolePort grantUserAssessmentRolePort;

    @Mock
    private CreateAssessmentSpaceUserAccessPort createSpaceUserAccessPort;

    @Test
    void testGrantAssessmentUserRole_CurrentUserIsNotAuthorized_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.grantAssessmentUserRole(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(spaceAccessChecker, grantUserAssessmentRolePort);
    }

    @Test
    void testGrantAssessmentUserRole_UserIsNotSpaceMember_AddUserToSpace() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(spaceAccessChecker.hasAccess(param.getAssessmentId(), param.getUserId())).thenReturn(false);

        doNothing().when(createSpaceUserAccessPort).persist(any());

        service.grantAssessmentUserRole(param);

        ArgumentCaptor<CreateAssessmentSpaceUserAccessPort.Param> createSpaceUserAccessPortParam =
            ArgumentCaptor.forClass(CreateAssessmentSpaceUserAccessPort.Param.class);
        verify(createSpaceUserAccessPort, times(1)).persist(createSpaceUserAccessPortParam.capture());

        assertEquals(param.getAssessmentId(), createSpaceUserAccessPortParam.getValue().assessmentId());
        assertEquals(param.getUserId(), createSpaceUserAccessPortParam.getValue().userId());
        assertEquals(param.getCurrentUserId(), createSpaceUserAccessPortParam.getValue().createdBy());
        assertNotNull(createSpaceUserAccessPortParam.getValue().creationTime());

        verify(grantUserAssessmentRolePort, times(1))
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }

    @Test
    void testGrantAssessmentUserRole_ValidParam_GrantAccess() {
        Param param = new Param(UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            .thenReturn(true);

        when(spaceAccessChecker.hasAccess(param.getAssessmentId(), param.getUserId())).thenReturn(true);

        doNothing().when(grantUserAssessmentRolePort)
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());

        service.grantAssessmentUserRole(param);

        verify(grantUserAssessmentRolePort, times(1))
            .persist(param.getAssessmentId(), param.getUserId(), param.getRoleId());
    }
}
