package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UpdateExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
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

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExpertGroupPictureServiceTest {

    @InjectMocks
    UpdateExpertGroupPictureService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    LoadExpertGroupPort loadExpertGroupPort;

    @Mock
    DeleteFilePort deleteFilePort;

    @Mock
    UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Mock
    UpdateExpertGroupPicturePort updateExpertGroupPicturePort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    @DisplayName("An existing expert group should undergo updating.")
    void testUpdateExpertGroupPicture_expertGroupNotExist_resourceNotFound() throws IOException {
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
            deleteFilePort,
            uploadExpertGroupPicturePort,
            updateExpertGroupPicturePort,
            createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Updating an expert group picture should be done by the owner of the expert group.")
    void testUpdateExpertGroupPicture_currentUserIsNotOwner_accessDenied() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, () -> service.update(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(loadExpertGroupPort,
            deleteFilePort,
            uploadExpertGroupPicturePort,
            updateExpertGroupPicturePort,
            createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("If the expert group already has a picture, it should be updated")
    void testUpdateExpertGroupPicture_alreadyHasPicture_fileUpdate() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "oldPicturePath", "website", currentUserId);
        String newPicturePath = "newPicturePath";
        String downloadLink = "downloadLink";

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        doNothing().when(deleteFilePort).deletePicture(expertGroup.getPicture());
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(newPicturePath);
        doNothing().when(updateExpertGroupPicturePort).updatePicture(expertGroupId, newPicturePath);
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn(downloadLink);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(loadExpertGroupPort).loadExpertGroup(expertGroupId);
        verify(deleteFilePort).deletePicture(expertGroup.getPicture());
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
    }

    @Test
    @DisplayName("If the expert group does not have a picture (null), it should be uploaded.")
    void testUpdateExpertGroupPicture_doesNotHavePicture_pictureUpload() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", null, "website", currentUserId);
        String newPicturePath = "newPicturePath";
        String downloadLink = "downloadLink";

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(newPicturePath);
        doNothing().when(updateExpertGroupPicturePort).updatePicture(expertGroupId, newPicturePath);
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn(downloadLink);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(loadExpertGroupPort).loadExpertGroup(expertGroupId);
        verifyNoInteractions(deleteFilePort);
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
    }

    @Test
    @DisplayName("If the expert group does not have a picture (blank), it should be uploaded")
    void testUpdateExpertGroupPicture_pictureIsBlank_uploadPicture() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);
        ExpertGroup expertGroup = new ExpertGroup(expertGroupId, "title", "bio",
            "about", "", "website", currentUserId);
        String newPicturePath = "newPicturePath";
        String downloadLink = "downloadLink";

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        verifyNoInteractions(deleteFilePort);
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(newPicturePath);
        doNothing().when(updateExpertGroupPicturePort).updatePicture(expertGroupId, newPicturePath);
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn(downloadLink);

        assertDoesNotThrow(() -> service.update(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(loadExpertGroupPort).loadExpertGroup(expertGroupId);
        verifyNoInteractions(deleteFilePort);
        verify(uploadExpertGroupPicturePort).uploadPicture(picture);
        verify(createFileDownloadLinkPort).createDownloadLink(any(), any());
    }
}

