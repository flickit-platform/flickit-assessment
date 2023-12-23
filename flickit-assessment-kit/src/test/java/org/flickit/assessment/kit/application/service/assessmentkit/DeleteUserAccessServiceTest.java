package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteKitUserAccessUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.LoadKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByEmailPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_KIT_USER_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.KitUserMother.simpleKitUser;
import static org.flickit.assessment.kit.test.fixture.application.UserMother.userWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessServiceTest {

    private static final String EMAIL = "example@email.com";
    @InjectMocks
    private DeleteKitUserAccessService service;
    @Mock
    private DeleteKitUserAccessPort deleteKitUserAccessPort;
    @Mock
    private LoadKitUserAccessPort loadKitUserAccessPort;
    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    @Mock
    private LoadUserByEmailPort loadUserByEmailPort;

    @Test
    void testDeleteUserAccess_ValidInputs_Delete() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadUserByEmailPort.loadByEmail(EMAIL)).thenReturn(Optional.of(userWithId(currentUserId)));
        when(loadKitUserAccessPort.loadByKitIdAndUserEmail(kitId, currentUserId)).thenReturn(Optional.of(simpleKitUser()));
        doNothing().when(deleteKitUserAccessPort).delete(new DeleteKitUserAccessPort.Param(kitId, EMAIL));

        var param = new DeleteKitUserAccessUseCase.Param(kitId, EMAIL, currentUserId);
        service.delete(param);

        ArgumentCaptor<DeleteKitUserAccessPort.Param> deletePortParam = ArgumentCaptor.forClass(DeleteKitUserAccessPort.Param.class);
        verify(deleteKitUserAccessPort).delete(deletePortParam.capture());

        assertEquals(kitId, deletePortParam.getValue().kitId());
        assertEquals(EMAIL, deletePortParam.getValue().email());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        var expertGroupOwnerId = UUID.randomUUID();
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(expertGroupOwnerId));

        var param = new DeleteKitUserAccessUseCase.Param(kitId, EMAIL, currentUserId);
        var exception = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroupId(any());
        verify(loadKitUserAccessPort, never()).loadByKitIdAndUserEmail(any(), any());
        verify(deleteKitUserAccessPort, never()).delete(any(DeleteKitUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_UserNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var param = new DeleteKitUserAccessUseCase.Param(kitId, EMAIL, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(EXPERT_GROUP_ID_NOT_FOUND, exception.getMessage());
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(loadKitUserAccessPort, never()).loadByKitIdAndUserEmail(any(), any());
        verify(deleteKitUserAccessPort, never()).delete(any(DeleteKitUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_UserAccessNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        UUID currentUserId = UUID.randomUUID();

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadUserByEmailPort.loadByEmail(EMAIL)).thenReturn(Optional.of(userWithId(currentUserId)));
        when(loadKitUserAccessPort.loadByKitIdAndUserEmail(kitId, currentUserId)).thenReturn(Optional.empty());

        var param = new DeleteKitUserAccessUseCase.Param(kitId, EMAIL, currentUserId);
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(DELETE_KIT_USER_ACCESS_KIT_USER_NOT_FOUND, exception.getMessage());
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroupId(any());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(any());
        verify(loadKitUserAccessPort, times(1)).loadByKitIdAndUserEmail(any(), any());
        verify(deleteKitUserAccessPort, never()).delete(any(DeleteKitUserAccessPort.Param.class));
    }


}
