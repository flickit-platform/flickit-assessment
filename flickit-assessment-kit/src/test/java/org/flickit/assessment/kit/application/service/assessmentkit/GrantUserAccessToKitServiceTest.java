package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE;
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
    private LoadKitExpertGroupPort loadExpertGroupIdPort;

    @Mock
    private GrantUserAccessToKitPort grantUserAccessToKitPort;

    @Test
    void testGrantUserAccessToKit_ValidParams_AddUserSuccessfully() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            currentUserId
        );
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadKitExpertGroupId(param.getKitId())).thenReturn(expertGroupId);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserId())).thenReturn(true);

        service.grantUserAccessToKit(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupIdPort, times(1)).loadKitExpertGroupId(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        var LoadExpertGroupOwnerParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(LoadExpertGroupOwnerParam.capture());
        assertEquals(expertGroupId, LoadExpertGroupOwnerParam.getValue());

        var grantAccessKitIdParam = ArgumentCaptor.forClass(Long.class);
        var grantAccessUserIdParam = ArgumentCaptor.forClass(UUID.class);
        verify(grantUserAccessToKitPort, times(1))
            .grantUserAccess(grantAccessKitIdParam.capture(), grantAccessUserIdParam.capture());
        assertEquals(param.getKitId(), grantAccessKitIdParam.getValue());
        assertEquals(param.getUserId(), grantAccessUserIdParam.getValue());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            currentUserId
        );
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadKitExpertGroupId(param.getKitId())).thenReturn(expertGroupId);
        var expertGroupOwnerId = UUID.randomUUID();
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(expertGroupOwnerId));

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }

    @Test
    void testGrantUserAccessToKit_InvalidKitIdExpertGroupOwnerNull_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            currentUserId
        );
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadKitExpertGroupId(param.getKitId())).thenReturn(expertGroupId);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(EXPERT_GROUP_ID_NOT_FOUND, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadKitExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }

    @Test
    void testGrantUserAccessToKit_DuplicateUserForKit_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            currentUserId
        );
        var expertGroupId = 3L;
        when(loadExpertGroupIdPort.loadKitExpertGroupId(param.getKitId())).thenReturn(expertGroupId);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserId())).thenReturn(false);

        var exception = assertThrows(ResourceAlreadyExistsException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadKitExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(grantUserAccessToKitPort, times(1)).grantUserAccess(any(), any());
    }
}
