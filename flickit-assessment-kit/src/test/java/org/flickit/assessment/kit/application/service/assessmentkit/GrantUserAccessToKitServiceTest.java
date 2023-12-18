package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerIdPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAccessToKitServiceTest {

    @InjectMocks
    private GrantUserAccessToKitService service;

    @Mock
    private LoadExpertGroupOwnerIdPort loadExpertGroupOwnerIdPort;

    @Mock
    private GrantUserAccessToKitPort grantUserAccessToKitPort;

    @Test
    void testGrantUserAccessToKit_ValidParams_AddUserSuccessfully() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            currentUserId
        );
        when(loadExpertGroupOwnerIdPort.loadByKitId(param.getKitId())).thenReturn(currentUserId);
        when(grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserEmail()))
            .thenReturn(true);

        service.grantUserAccessToKit(param);

        var LoadExpertGroupOwnerParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupOwnerIdPort, times(1)).loadByKitId(LoadExpertGroupOwnerParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupOwnerParam.getValue());

        var grantAccessKitIdParam = ArgumentCaptor.forClass(Long.class);
        var grantAccessUserEmailParam = ArgumentCaptor.forClass(String.class);
        verify(grantUserAccessToKitPort, times(1))
            .grantUserAccess(grantAccessKitIdParam.capture(), grantAccessUserEmailParam.capture());
        assertEquals(param.getKitId(), grantAccessKitIdParam.getValue());
        assertEquals(param.getUserEmail(), grantAccessUserEmailParam.getValue());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            currentUserId
        );
        var expertGroupOwnerId = UUID.randomUUID();
        when(loadExpertGroupOwnerIdPort.loadByKitId(param.getKitId())).thenReturn(expertGroupOwnerId);

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadExpertGroupOwnerIdPort, times(1)).loadByKitId(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }
}
