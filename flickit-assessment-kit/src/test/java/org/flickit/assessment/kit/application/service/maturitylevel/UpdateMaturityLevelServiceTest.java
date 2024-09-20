package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMaturityLevelServiceTest {

    @InjectMocks
    UpdateMaturityLevelService service;

    @Mock
    LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Mock
    UpdateMaturityLevelPort updateMaturityLevelPort;

    @Test
    void testUpdateMaturityLevelService_KitNotFound_ResourceNotFoundException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateMaturityLevel(param));

        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());
        verifyNoInteractions(loadActiveKitVersionIdPort, updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelService_ExpertGroupNotFound_ResourceNotFoundException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(null);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateMaturityLevel(param));

        assertEquals(EXPERT_GROUP_ID_NOT_FOUND, throwable.getMessage());
        verifyNoInteractions(loadActiveKitVersionIdPort, updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelService_UserIsNotExpertGroupOwner_AccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        var expertGroup = ExpertGroupMother.createExpertGroup();
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateMaturityLevel(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(loadActiveKitVersionIdPort, updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelService_KitIdNotFound_AccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        var expertGroup = ExpertGroupMother.createExpertGroupWithCreatedBy(currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateMaturityLevel(param));

        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testUpdateMaturityLevelService_ValidParameters_SuccessfulUpdate() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        var expertGroup = ExpertGroupMother.createExpertGroupWithCreatedBy(currentUserId);
        var kitVersionId = 321L;

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        doNothing().when(updateMaturityLevelPort).updateInfo(any(), any(), any(), any());

        assertDoesNotThrow(() -> service.updateMaturityLevel(param));
    }
}
