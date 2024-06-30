package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupPictureUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupPictureServiceTest {

    @InjectMocks
    DeleteExpertGroupPictureService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    DeleteFilePort deleteFilePort;

    @Mock
    LoadExpertGroupPort loadExpertGroupPort;

    @Mock
    UpdateExpertGroupPicturePort updateExpertGroupPicturePort;

    @Test
    @DisplayName("Deleting an expert group picture should be done on an existing expert group.")
    void testDeleteExpertGroupPicture_expertGroupNotExist_resourceNotFound() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param), EXPERT_GROUP_ID_NOT_FOUND);
        assertEquals(EXPERT_GROUP_ID_NOT_FOUND, throwable.getMessage());
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(loadExpertGroupPort,
            updateExpertGroupPicturePort,
            deleteFilePort);
    }

    @Test
    @DisplayName("Deleting an expert group picture should be done by the owner of the expert group.")
    void testDeleteExpertGroupPicture_currentUserIsNotOwner_accessDenied() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.delete(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteFilePort);
        verifyNoInteractions(loadExpertGroupPort,
            deleteFilePort,
            updateExpertGroupPicturePort);
    }

    @Test
    @DisplayName("Deleting an expert group picture should be done on an expert group already has a picture")
    void testDeleteExpertGroupPicture_alreadyHasPicture_fileDelete() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "picturePath", "website", currentUserId);
        String picturePath = "picturePath";

        doNothing().when(deleteFilePort).deletePicture(picturePath);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.delete(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(deleteFilePort).deletePicture(picturePath);
    }

    @Test
    @DisplayName("Deleting an expert group picture won't take action if no picture (null).")
    void testRemoveExpertGroupPicture_doesNotHavePicture_doNothing() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", null, "website", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.delete(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteFilePort,
            updateExpertGroupPicturePort);
    }

    @Test
    @DisplayName("Deleting an expert group picture won't take action if there is no picture (blank).")
    void testRemoveExpertGroupPicture_pictureIsBlank_doNothing() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);

        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "", "website", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.delete(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteFilePort,
            updateExpertGroupPicturePort);
    }
}
