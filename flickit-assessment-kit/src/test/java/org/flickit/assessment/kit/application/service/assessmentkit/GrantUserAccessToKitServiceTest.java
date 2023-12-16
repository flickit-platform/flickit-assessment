package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.exception.InvalidActionException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitOwnerPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.flickit.assessment.kit.test.fixture.application.UserMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_NOT_KIT_OWNER;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_USER_ALREADY_EXIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAccessToKitServiceTest {

    @InjectMocks
    private GrantUserAccessToKitService service;

    @Mock
    private LoadAssessmentKitOwnerPort loadKitOwnerPort;

    @Mock
    private GrantUserAccessToKitPort grantUserAccessToKitPort;

    @Test
    void testGrantUserAccessToKit_ValidParams_AddUserSuccessfully() {
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            UUID.randomUUID()
        );
        User currentUser = UserMother.userWithId(param.getCurrentUserId());
        when(loadKitOwnerPort.loadKitOwnerById(param.getKitId())).thenReturn(currentUser);
        when(grantUserAccessToKitPort.grantUserAccessToKitByUserEmail(param.getKitId(), param.getUserEmail()))
            .thenReturn(true);

        service.grantUserAccessToKit(param);

        var loadKitOwnerParam = ArgumentCaptor.forClass(Long.class);
        verify(loadKitOwnerPort, times(1)).loadKitOwnerById(loadKitOwnerParam.capture());
        assertEquals(param.getKitId(), loadKitOwnerParam.getValue());

        var grantAccessKitIdParam = ArgumentCaptor.forClass(Long.class);
        var grantAccessUserEmailParam = ArgumentCaptor.forClass(String.class);
        verify(grantUserAccessToKitPort, times(1))
            .grantUserAccessToKitByUserEmail(grantAccessKitIdParam.capture(), grantAccessUserEmailParam.capture());
        assertEquals(param.getKitId(), grantAccessKitIdParam.getValue());
        assertEquals(param.getUserEmail(), grantAccessUserEmailParam.getValue());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            UUID.randomUUID()
        );
        User kitOwner = UserMother.userWithId(UUID.randomUUID());
        when(loadKitOwnerPort.loadKitOwnerById(param.getKitId())).thenReturn(kitOwner);

        var exception = assertThrows(InvalidActionException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_NOT_KIT_OWNER, exception.getMessage());
        verify(loadKitOwnerPort, times(1)).loadKitOwnerById(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccessToKitByUserEmail(any(), any());
    }

    @Test
    void testGrantUserAccessToKit_UserAlreadyHasAccess_ThrowsException() {
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            UUID.randomUUID()
        );
        User currentUser = UserMother.userWithId(param.getCurrentUserId());
        when(loadKitOwnerPort.loadKitOwnerById(param.getKitId())).thenReturn(currentUser);
        when(grantUserAccessToKitPort.grantUserAccessToKitByUserEmail(param.getKitId(), param.getUserEmail()))
            .thenReturn(false);

        var exception = assertThrows(InvalidActionException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(GRANT_USER_ACCESS_TO_KIT_USER_ALREADY_EXIST, exception.getMessage());
        verify(loadKitOwnerPort, times(1)).loadKitOwnerById(any());
        verify(grantUserAccessToKitPort, times(1)).grantUserAccessToKitByUserEmail(any(), any());
    }
}
