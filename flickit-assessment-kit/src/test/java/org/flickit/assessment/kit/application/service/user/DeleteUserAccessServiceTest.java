package org.flickit.assessment.kit.application.service.user;

import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.user.DeleteUserAccessUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitByIdPort;
import org.flickit.assessment.kit.application.port.out.kituser.LoadKitUserByKitAndUserPort;
import org.flickit.assessment.kit.application.port.out.user.DeleteUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitUserMother.simpleKitUser;
import static org.flickit.assessment.kit.test.fixture.application.UserMother.simpleUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccessServiceTest {

    @InjectMocks
    private DeleteUserAccessService service;

    @Mock
    private DeleteUserAccessPort deleteUserAccessPort;

    @Mock
    private LoadKitByIdPort loadKitByIdPort;

    @Mock
    private LoadUserByIdPort loadUserByIdPort;

    @Mock
    private LoadKitUserByKitAndUserPort loadKitUserByKitAndUserPort;

    @Test
    void testDeleteUserAccess_ValidInputs_Delete() {
        Long kitId = 1L;
        Long userId = 1L;

        doNothing().when(deleteUserAccessPort).delete(new DeleteUserAccessPort.Param(kitId, userId));
        when(loadKitByIdPort.load(kitId)).thenReturn(Optional.of(simpleKit()));
        when(loadUserByIdPort.load(userId)).thenReturn(Optional.of(simpleUser()));
        when(loadKitUserByKitAndUserPort.loadByKitAndUser(kitId, userId)).thenReturn(Optional.of(simpleKitUser()));

        var param = new DeleteUserAccessUseCase.Param(kitId, userId);
        service.delete(param);

        ArgumentCaptor<DeleteUserAccessPort.Param> deletePortParam = ArgumentCaptor.forClass(DeleteUserAccessPort.Param.class);
        verify(deleteUserAccessPort).delete(deletePortParam.capture());

        assertEquals(kitId, deletePortParam.getValue().kitId());
        assertEquals(userId, deletePortParam.getValue().userId());
    }

    @Test
    void testDeleteUserAccess_KitNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long userId = 1L;

        when(loadKitByIdPort.load(kitId)).thenReturn(Optional.empty());

        var param = new DeleteUserAccessUseCase.Param(kitId, userId);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));
        assertThat(throwable).hasMessage(DELETE_USER_ACCESS_KIT_NOT_FOUND);
    }

    @Test
    void testDeleteUserAccess_UserNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long userId = 1L;

        when(loadKitByIdPort.load(kitId)).thenReturn(Optional.of(simpleKit()));
        when(loadUserByIdPort.load(userId)).thenReturn(Optional.empty());

        var param = new DeleteUserAccessUseCase.Param(kitId, userId);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));
        assertThat(throwable).hasMessage(DELETE_USER_ACCESS_USER_NOT_FOUND);
    }

    @Test
    void testDeleteUserAccess_UserAccessNotFound_ErrorMessage() {
        Long kitId = 1L;
        Long userId = 1L;

        when(loadKitByIdPort.load(kitId)).thenReturn(Optional.of(simpleKit()));
        when(loadUserByIdPort.load(userId)).thenReturn(Optional.of(simpleUser()));
        when(loadKitUserByKitAndUserPort.loadByKitAndUser(kitId, userId)).thenReturn(Optional.empty());

        var param = new DeleteUserAccessUseCase.Param(kitId, userId);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));
        assertThat(throwable).hasMessage(DELETE_USER_ACCESS_KIT_USER_NOT_FOUND);
    }


}
