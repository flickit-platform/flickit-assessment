package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAccessToKitServiceTest {

    @InjectMocks
    private GrantUserAccessToKitService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadExpertGroupIdPort loadExpertGroupIdPort;

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
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadExpertGroupId(param.getKitId())).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        doNothing().when(grantUserAccessToKitPort).grantUserAccess(param.getKitId(), param.getEmail());

        service.grantUserAccessToKit(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        var LoadExpertGroupOwnerParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(LoadExpertGroupOwnerParam.capture());
        assertEquals(expertGroupId, LoadExpertGroupOwnerParam.getValue());

        var grantAccessKitIdParam = ArgumentCaptor.forClass(Long.class);
        var grantAccessEmailParam = ArgumentCaptor.forClass(String.class);
        verify(grantUserAccessToKitPort, times(1))
            .grantUserAccess(grantAccessKitIdParam.capture(), grantAccessEmailParam.capture());
        assertEquals(param.getKitId(), grantAccessKitIdParam.getValue());
        assertEquals(param.getEmail(), grantAccessEmailParam.getValue());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            currentUserId
        );
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadExpertGroupId(param.getKitId())).thenReturn(Optional.of(expertGroupId));
        var expertGroupOwnerId = UUID.randomUUID();
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(expertGroupOwnerId));

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }

    @Test
    void testGrantUserAccessToKit_InvalidKitId_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            currentUserId
        );
        when(loadExpertGroupIdPort.loadExpertGroupId(param.getKitId())).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, never()).loadOwnerId(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }

    @Test
    void testGrantUserAccessToKit_InvalidKitIdExpertGroupOwnerNull_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            "user@email.com",
            currentUserId
        );
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadExpertGroupId(param.getKitId())).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(GRANT_USER_ACCESS_TO_KIT_EXPERT_GROUP_OWNER_NOT_FOUND, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }
}
