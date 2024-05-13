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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

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
    UpdateExpertGroupPictureFilePort updateExpertGroupPictureFilePort;

    @Mock
    UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Mock
    LoadExpertGroupPort loadExpertGroupPort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Mock
    UpdateExpertGroupPicturePort updateExpertGroupPicturePort;


    @Test
    @DisplayName("Updating an expert group should be done on an existing expert group")
    void testUpdateExpertGroupPicture_expertGroupInvalid_resourceNotFound() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> service.update(param), EXPERT_GROUP_ID_NOT_FOUND);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(loadExpertGroupPort,
            updateExpertGroupPicturePort,
            updateExpertGroupPictureFilePort,
            uploadExpertGroupPicturePort,
            createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Updating an expert group picture should be done by the owner of the expert group.")
    void testUpdateExpertGroupPicture_currentUserNotOwner_accessDenied() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, () -> service.update(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(updateExpertGroupPictureFilePort);
        verifyNoInteractions(loadExpertGroupPort,
            updateExpertGroupPictureFilePort,
            updateExpertGroupPicturePort,
            uploadExpertGroupPicturePort,
            createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("If the expert group already has a picture, it should be updated")
    void testUpdateExpertGroupPicture_alreadyHavePicture_shouldDelete() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "picturePath", "website", currentUserId);
        String picturePath = "picturePath";

        when(updateExpertGroupPictureFilePort.updatePicture(picture, picturePath)).thenReturn(picturePath);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(updateExpertGroupPictureFilePort).updatePicture(picture, picturePath);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
        verifyNoInteractions(uploadExpertGroupPicturePort, updateExpertGroupPicturePort);
    }

    @Test
    @DisplayName("If the expert group does not have a picture, it should be uploaded")
    void testUpdateExpertGroupPicture_pictureIsNull_shouldDelete() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
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
        verifyNoInteractions(updateExpertGroupPictureFilePort);
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
        verify(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);
    }

    @Test
    @DisplayName("If the expert group does not have a picture, it should be uploaded")
    void testUpdateExpertGroupPicture_pictureIsBlank_shouldDelete() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "", "website", currentUserId);
        String picturePath = "picturePath";

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(picturePath);
        doNothing().when(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(updateExpertGroupPictureFilePort);
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
        verify(updateExpertGroupPicturePort).updatePicture(expertGroupId, picturePath);
    }
}
