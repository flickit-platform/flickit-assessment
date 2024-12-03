package org.flickit.assessment.users.application.service.user;

import lombok.SneakyThrows;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfilePictureUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPicturePort;
import org.flickit.assessment.users.application.port.out.user.UploadUserProfilePicturePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_PICTURE_SIZE_MAX;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;
import static org.flickit.assessment.users.test.fixture.application.UserMother.createUser;
import static org.junit.jupiter.api.Assertions.*;
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
    private UpdateUserPicturePort updateUserPicturePort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testUpdateUserProfilePicture_WhenFileSizeIsInvalid_ShouldThrowValidationError() {
        MockMultipartFile bigPicture = new MockMultipartFile("images", "image1",
            "image/jpeg", new byte[2]);
        var param = createParam(b -> b.picture(bigPicture));

        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofBytes(1));

        var throwable = assertThrows(ValidationException.class, () -> service.update(param));
        assertEquals(UPLOAD_FILE_PICTURE_SIZE_MAX, throwable.getMessageKey());

        verifyNoInteractions(deleteFilePort, uploadUserProfilePicturePort, updateUserPicturePort);
    }

    @Test
    void testUpdateUserProfilePicture_WhenContentTypeIsInvalid_ShouldReturnValidationError() {
        MockMultipartFile invalidFormatPicture = new MockMultipartFile("images", "image1",
            "application/zip", new byte[1]);
        var param = createParam(b -> b.picture(invalidFormatPicture));

        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofBytes(2));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));

        var throwable = assertThrows(ValidationException.class, () -> service.update(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());

        verifyNoInteractions(deleteFilePort, uploadUserProfilePicturePort, updateUserPicturePort);
    }

    @Test
    void testUpdateUserProfilePicture_WhenCurrentUserIdDoesNotExist_ShouldThrowNotFoundException() {
        var param = createParam(UpdateUserProfilePictureUseCase.Param.ParamBuilder::build);

        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofBytes(2));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));
        when(loadUserPort.loadUser(param.getCurrentUserId())).thenThrow(new ResourceNotFoundException(USER_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.update(param));
        assertEquals(USER_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(deleteFilePort, uploadUserProfilePicturePort, updateUserPicturePort);
    }

    @Test
    void testUpdateUserProfilePicture_WhenExistingPicture_DeletesPreviousPicture() {
        var param = createParam(UpdateUserProfilePictureUseCase.Param.ParamBuilder::build);
        var user = createUser(param.getCurrentUserId(), "picture/old-path");
        var uploadedFilePath = "picture/path";

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenReturn(user);
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofBytes(2));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));
        doNothing().when(deleteFilePort).deletePicture(user.getPicturePath());
        when(uploadUserProfilePicturePort.uploadUserProfilePicture(param.getPicture())).thenReturn(uploadedFilePath);
        doNothing().when(updateUserPicturePort).updatePicture(param.getCurrentUserId(), uploadedFilePath);
        when(createFileDownloadLinkPort.createDownloadLink(uploadedFilePath, Duration.ofDays(1))).thenReturn("link/to/file");

        assertDoesNotThrow(() ->service.update(param));
    }

    @Test
    void testUpdateUserProfilePicture_DoesNotHavePicture_ShouldSuccessfulWithoutDeletingAnything() {
        var param = createParam(UpdateUserProfilePictureUseCase.Param.ParamBuilder::build);
        var user = createUser(param.getCurrentUserId(), null);
        var uploadedFilePath = "picture/path";

        when(loadUserPort.loadUser(param.getCurrentUserId())).thenReturn(user);
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofBytes(2));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));
        when(uploadUserProfilePicturePort.uploadUserProfilePicture(param.getPicture())).thenReturn(uploadedFilePath);
        doNothing().when(updateUserPicturePort).updatePicture(param.getCurrentUserId(), uploadedFilePath);
        when(createFileDownloadLinkPort.createDownloadLink(uploadedFilePath, Duration.ofDays(1))).thenReturn("link/to/file");

        service.update(param);

        verifyNoInteractions(deleteFilePort);
    }

    @SneakyThrows
    private UpdateUserProfilePictureUseCase.Param createParam(Consumer<UpdateUserProfilePictureUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    @SneakyThrows
    private UpdateUserProfilePictureUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateUserProfilePictureUseCase.Param.builder()
            .currentUserId(UUID.randomUUID())
            .picture(new MockMultipartFile("images", "image1",
                "image/jpeg", new byte[1]));
    }
}
