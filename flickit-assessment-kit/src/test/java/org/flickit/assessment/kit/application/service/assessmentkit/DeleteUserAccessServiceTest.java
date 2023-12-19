package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteUserAccessOnKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.LoadKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.useraccess.DeleteUserAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.test.fixture.application.KitUserMother.simpleKitUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessServiceTest {

    private static final String EMAIL = "example@email.com";
    @InjectMocks
    private DeleteUserAccessOnKitService service;
    @Mock
    private DeleteUserAccessPort deleteUserAccessPort;
    @Mock
    private LoadKitUserAccessPort loadKitUserAccessPort;
    @Mock
    private LoadExpertGroupIdPort loadExpertGroupIdPort;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    void testDeleteUserAccess_ValidInputs_Delete() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();

        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadKitUserAccessPort.load(kitId, currentUserId)).thenReturn(Optional.of(simpleKitUser()));
        doNothing().when(deleteUserAccessPort).delete(new DeleteUserAccessPort.Param(kitId, EMAIL));

        var param = new DeleteUserAccessOnKitUseCase.Param(kitId, EMAIL, currentUserId);
        service.delete(param);

        ArgumentCaptor<DeleteUserAccessPort.Param> deletePortParam = ArgumentCaptor.forClass(DeleteUserAccessPort.Param.class);
        verify(deleteUserAccessPort).delete(deletePortParam.capture());

        assertEquals(kitId, deletePortParam.getValue().kitId());
        assertEquals(EMAIL, deletePortParam.getValue().email());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();

        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.of(expertGroupId));
        var expertGroupOwnerId = UUID.randomUUID();
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(expertGroupOwnerId));

        var param = new DeleteUserAccessOnKitUseCase.Param(kitId, EMAIL, currentUserId);
        var exception = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(any());
        verify(loadKitUserAccessPort, never()).load(any(), any());
        verify(deleteUserAccessPort, never()).delete(any(DeleteUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_KitNotFound_ErrorMessage() {
        Long kitId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteUserAccessOnKitUseCase.Param(kitId, EMAIL, currentUserId);

        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(DELETE_USER_ACCESS_KIT_ID_NOT_FOUND, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, never()).loadOwnerId(any());
        verify(loadKitUserAccessPort, never()).load(any(), any());
        verify(deleteUserAccessPort, never()).delete(any(DeleteUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_UserNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteUserAccessOnKitUseCase.Param(kitId, EMAIL, currentUserId);

        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(DELETE_USER_ACCESS_EXPERT_GROUP_OWNER_NOT_FOUND, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(loadKitUserAccessPort, never()).load(any(), any());
        verify(deleteUserAccessPort, never()).delete(any(DeleteUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_UserAccessNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();

        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadKitUserAccessPort.load(kitId, currentUserId)).thenReturn(Optional.empty());

        var param = new DeleteUserAccessOnKitUseCase.Param(kitId, EMAIL, currentUserId);
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(DELETE_USER_ACCESS_KIT_USER_NOT_FOUND, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(loadKitUserAccessPort, times(1)).load(any(), any());
        verify(deleteUserAccessPort, never()).delete(any(DeleteUserAccessPort.Param.class));
    }


}
