package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.RemoveExpertGroupPictureUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
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
class RemoveExpertGroupPictureServiceTest {

    @InjectMocks
    RemoveExpertGroupPictureService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    DeleteExpertGroupPictureFilePort deleteExpertGroupPictureFilePort;


    @Mock
    LoadExpertGroupPort loadExpertGroupPort;

    @Mock
    UpdateExpertGroupPicturePort updateExpertGroupPicturePort;


    @Test
    @DisplayName("An existing expert group should undergo deleting picture.")
    void testDeleteExpertGroupPicture_expertGroupNotExist_resourceNotFound() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> service.remove(param), EXPERT_GROUP_ID_NOT_FOUND);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(loadExpertGroupPort,
            updateExpertGroupPicturePort,
            deleteExpertGroupPictureFilePort);
    }

    @Test
    @DisplayName("Deleting an expert group picture should be done by the owner of the expert group.")
    void testDeleteExpertGroupPicture_currentUserIsNotOwner_accessDenied() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, () -> service.remove(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPictureFilePort);
        verifyNoInteractions(loadExpertGroupPort,
            deleteExpertGroupPictureFilePort,
            updateExpertGroupPicturePort);
    }

    @Test
    @DisplayName("If the expert group already has a picture, it should be deleted")
    void testDeleteExpertGroupPicture_alreadyHasPicture_fileDelete() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "picturePath", "website", currentUserId);
        String picturePath = "picturePath";

        when(deleteExpertGroupPictureFilePort.deletePicture(picturePath)).thenReturn(picturePath);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.remove(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(deleteExpertGroupPictureFilePort).deletePicture(picturePath);
    }

    @Test
    @DisplayName("The deleting expert group picture won't take any action if there is no picture (null).")
    void testRemoveExpertGroupPicture_doesNotHavePicture_doNothing() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", null, "website", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.remove(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPictureFilePort,
            updateExpertGroupPicturePort);
    }

    @Test
    @DisplayName("The deleting expert group picture won't take any action if there is no picture (blank).")
    void testRemoveExpertGroupPicture_pictureIsBlank_doNothing() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, currentUserId);

        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "", "website", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.remove(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPictureFilePort,
            updateExpertGroupPicturePort);
    }
}
