package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteKitUserAccessUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.DeleteKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_KIT_USER_ACCESS_USER_IS_EXPERT_GROUP_OWNER;
import static org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.flickit.assessment.kit.test.fixture.application.UserMother.userWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessServiceTest {

    @InjectMocks
    private DeleteKitUserAccessService service;
    @Mock
    private DeleteKitUserAccessPort deleteKitUserAccessPort;
    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;
    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;
    @Mock
    private LoadUserPort loadUserPort;

    @Test
    void testDeleteUserAccess_ValidInputs_Delete() {
        Long kitId = 1L;
        UUID userId = UUID.randomUUID();
        ExpertGroup expertGroup = createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(loadUserPort.loadById(userId)).thenReturn(Optional.of(userWithId(userId)));
        when(checkKitUserAccessPort.hasAccess(kitId, userId)).thenReturn(true);
        doNothing().when(deleteKitUserAccessPort).delete(new DeleteKitUserAccessPort.Param(kitId, userId));

        var param = new DeleteKitUserAccessUseCase.Param(kitId, userId, expertGroup.getOwnerId());
        service.delete(param);

        ArgumentCaptor<DeleteKitUserAccessPort.Param> deletePortParam = ArgumentCaptor.forClass(DeleteKitUserAccessPort.Param.class);
        verify(deleteKitUserAccessPort).delete(deletePortParam.capture());

        assertEquals(kitId, deletePortParam.getValue().kitId());
        assertEquals(userId, deletePortParam.getValue().userId());
    }

    @Test
    void testDeleteUserAccess_InvalidCurrentUser_ThrowsException() {
        Long kitId = 1L;
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ExpertGroup expertGroup = createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);

        var param = new DeleteKitUserAccessUseCase.Param(kitId, userId, currentUserId);
        var exception = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroup(any());
        verify(checkKitUserAccessPort, never()).hasAccess(any(), any());
        verify(deleteKitUserAccessPort, never()).delete(any(DeleteKitUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_UserNotFound_ErrorMessage() {
        Long kitId = 1L;
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ExpertGroup expertGroup = createExpertGroup();

        var param = new DeleteKitUserAccessUseCase.Param(kitId, userId, currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroup(any());
        verify(checkKitUserAccessPort, never()).hasAccess(any(), any());
        verify(deleteKitUserAccessPort, never()).delete(any(DeleteKitUserAccessPort.Param.class));
    }

    @Test
    void testDeleteUserAccess_UserAccessNotFound_ErrorMessage() {
        Long kitId = 1L;
        UUID currentUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ExpertGroup expertGroup = createExpertGroup();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);

        var param = new DeleteKitUserAccessUseCase.Param(kitId, userId, currentUserId);
        var exception = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroup(any());
        verifyNoInteractions(checkKitUserAccessPort);
        verifyNoInteractions(loadUserPort);
        verifyNoInteractions(deleteKitUserAccessPort);
    }

    @Test
    void testDeleteUserAccess_UserIsExpertGroupOwner_ErrorMessage() {
        ExpertGroup expertGroup = createExpertGroup();
        Long kitId = 1L;
        UUID currentUserId = expertGroup.getOwnerId();

        when(loadKitExpertGroupPort.loadKitExpertGroup(kitId)).thenReturn(expertGroup);
        when(loadUserPort.loadById(currentUserId)).thenReturn(Optional.of(userWithId(currentUserId)));
        when(checkKitUserAccessPort.hasAccess(kitId, currentUserId)).thenReturn(true);

        var param = new DeleteKitUserAccessUseCase.Param(kitId, currentUserId, expertGroup.getOwnerId());
        assertThrows(ValidationException.class, () -> service.delete(param),
            DELETE_KIT_USER_ACCESS_USER_IS_EXPERT_GROUP_OWNER);

        verify(loadKitExpertGroupPort, times(1)).loadKitExpertGroup(any());
        verifyNoInteractions(deleteKitUserAccessPort);
    }
}
