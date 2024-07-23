package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfilePictureUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserProfilePicturePort;
import org.flickit.assessment.users.application.port.out.user.UploadUserProfilePicturePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_PICTURE_SIZE_MAX;
import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfilePictureServiceTest {

    @InjectMocks
    UpdateUserProfilePictureService service;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private FileProperties fileProperties;

    @Mock
    private DeleteFilePort deleteFilePort;

    @Mock
    private UploadUserProfilePicturePort uploadUserProfilePicturePort;

    @Mock
    private UpdateUserProfilePicturePort updateUserProfilePicturePort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    @DisplayName("The file size should be based on the predefined circumstance.")
    void testUpdateProfile_FileSizeIsNotValid_ValidationError() {
        var currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new byte[6 * 1024 * 1024]);
        var param = new UpdateUserProfilePictureUseCase.Param(currentUserId, picture);

        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));

        var throwable = assertThrows(ValidationException.class, () -> service.update(param));
        assertEquals(UPLOAD_FILE_PICTURE_SIZE_MAX, throwable.getMessageKey());

        verify(fileProperties, times(1)).getPictureMaxSize();
        verifyNoInteractions(deleteFilePort, uploadUserProfilePicturePort, updateUserProfilePicturePort);
    }

    @Test
    @DisplayName("If the userId is not found, the updateProfilePictureService should return a resourceNotFound Error.")
    void testUpdateProfile_CurrentUserIdIsNotExists_NotFoundError() throws IOException {
        var currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        var param = new UpdateUserProfilePictureUseCase.Param(currentUserId, picture);

        when(loadUserPort.loadUser(currentUserId)).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.update(param));
        assertEquals(USER_ID_NOT_FOUND, throwable.getMessage());

        verify(loadUserPort).loadUser(currentUserId);
        verifyNoInteractions(deleteFilePort, uploadUserProfilePicturePort, updateUserProfilePicturePort);
    }

    @Test
    @DisplayName("The file content should be based on the predefined circumstance.")
    void testUpdateProfile_ContentTypeIsNotValid_ValidationError() {
        var currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "application/zip", new byte[4 * 1024 * 1024]);
        var param = new UpdateUserProfilePictureUseCase.Param(currentUserId, picture);

        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));

        var throwable = assertThrows(ValidationException.class, () -> service.update(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());

        verify(fileProperties).getPictureMaxSize();
        verify(fileProperties).getPictureContentTypes();
        verifyNoInteractions(deleteFilePort, uploadUserProfilePicturePort, updateUserProfilePicturePort);
    }

    @Test
    @DisplayName("If the user profile picture has been set previously, the update user profile picture service should be deleted first, and then updated.")
    void testUpdateProfile_HavePicture_PictureShouldBeDeleted() {
        var currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/jpeg", new byte[4 * 1024 * 1024]);
        var param = new UpdateUserProfilePictureUseCase.Param(currentUserId, picture);
        var user = new User(currentUserId, "email", "DisplayName", "bio", "linkedIn", "picturePath");
        var uploadedFilePath = "picture/path";

        when(loadUserPort.loadUser(currentUserId)).thenReturn(user);
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));
        doNothing().when(deleteFilePort).deletePicture(user.getPicturePath());
        when(uploadUserProfilePicturePort.uploadUserProfilePicture(param.getPicture())).thenReturn(uploadedFilePath);
        doNothing().when(updateUserProfilePicturePort).updatePicture(param.getUserId(), uploadedFilePath);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any())).thenReturn("link/to/file");

        assertDoesNotThrow(() -> service.update(param));

        verify(loadUserPort).loadUser(currentUserId);
        verify(fileProperties).getPictureMaxSize();
        verify(fileProperties).getPictureContentTypes();
        verify(deleteFilePort).deletePicture(user.getPicturePath());
        verify(uploadUserProfilePicturePort).uploadUserProfilePicture(param.getPicture());
        verify(updateUserProfilePicturePort).updatePicture(param.getUserId(), uploadedFilePath);
        verify(createFileDownloadLinkPort).createDownloadLink(anyString(), any());
    }

    @Test
    @DisplayName("If the user profile picture has not been set previously, the update user profile picture service should not delete anything.")
    void testUpdateProfile_DoesNotHavePicture_ShouldNotDeleted() {
        var currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/jpeg", new byte[4 * 1024 * 1024]);
        var param = new UpdateUserProfilePictureUseCase.Param(currentUserId, picture);
        var user = new User(currentUserId, "email", "DisplayName", "bio", "linkedIn", null);
        var uploadedFilePath = "picture/path";

        when(loadUserPort.loadUser(currentUserId)).thenReturn(user);
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));
        when(uploadUserProfilePicturePort.uploadUserProfilePicture(param.getPicture())).thenReturn(uploadedFilePath);
        doNothing().when(updateUserProfilePicturePort).updatePicture(param.getUserId(), uploadedFilePath);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any())).thenReturn("link/to/file");

        assertDoesNotThrow(() -> service.update(param));

        verify(loadUserPort).loadUser(currentUserId);
        verify(fileProperties).getPictureMaxSize();
        verify(fileProperties).getPictureContentTypes();
        verify(uploadUserProfilePicturePort).uploadUserProfilePicture(param.getPicture());
        verify(updateUserProfilePicturePort).updatePicture(param.getUserId(), uploadedFilePath);
        verify(createFileDownloadLinkPort).createDownloadLink(anyString(), any());
        verifyNoInteractions(deleteFilePort);
    }
}
