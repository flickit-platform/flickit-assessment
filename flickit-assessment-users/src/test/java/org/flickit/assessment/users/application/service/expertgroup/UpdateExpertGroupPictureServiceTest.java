package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.*;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UpdateExpertGroupPictureServiceTest {

    @InjectMocks
    UpdateExpertGroupPictureService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    DeleteExpertGroupPicturePort deleteExpertGroupPicturePort;

    @Mock
    UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Mock
    UpdateExpertGroupPicturePort updateExpertGroupPicturePort;

    @Mock
    LoadExpertGroupPort loadExpertGroupPort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;


    @Test
    @DisplayName("Updating an expert group should be done on an existing expert group")
    void testUpdateExpertGroupPicture_expertGroupInvalid_resourceNotFound() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/images/image1.png"));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> service.update(param), EXPERT_GROUP_ID_NOT_FOUND);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(loadExpertGroupPort,
            deleteExpertGroupPicturePort,
            uploadExpertGroupPicturePort,
            updateExpertGroupPicturePort,
            createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Updating an expert group picture should be done by the owner of the expert group.")
    void testUpdateExpertGroupPicture_currentUserNotOwner_accessDenied() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/images/image1.png"));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, () -> service.update(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPicturePort);
        verifyNoInteractions(loadExpertGroupPort,
            deleteExpertGroupPicturePort,
            uploadExpertGroupPicturePort,
            updateExpertGroupPicturePort,
            createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("If the expert group already has a picture, it should be removed")
    void testUpdateExpertGroupPicture_alreadyHavePicture_shouldDelete() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/images/image1.png"));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "picturePath", "website", currentUserId);
        String picturePath = "picturePath";

        doNothing().when(deleteExpertGroupPicturePort).deletePicture(picturePath);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(picturePath);
        doNothing().when(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(deleteExpertGroupPicturePort).deletePicture(picturePath);
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
    }

    @Test
    @DisplayName("If the expert group already does not have a picture, it should be removed")
    void testUpdateExpertGroupPicture_doesNotHavePicture_shouldDelete() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/images/image1.png"));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", null, "website", currentUserId);
        String picturePath = "picturePath";

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(picturePath);
        doNothing().when(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(deleteExpertGroupPicturePort);
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
    }
}
