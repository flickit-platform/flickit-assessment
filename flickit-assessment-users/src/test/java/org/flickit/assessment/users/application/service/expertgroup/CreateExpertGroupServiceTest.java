package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.expertgroup.CreateExpertGroupUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.CreateExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UploadExpertGroupPicturePort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
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
import java.util.Random;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_PICTURE_SIZE_MAX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateExpertGroupServiceTest {

    @InjectMocks
    private CreateExpertGroupService service;

    @Mock
    private FileProperties fileProperties;

    @Mock
    private CreateExpertGroupPort createExpertGroupPort;

    @Mock
    private CreateExpertGroupAccessPort createExpertGroupAccessPort;

    @Mock
    private UploadExpertGroupPicturePort uploadExpertGroupPicturePort;

    @Test
    @DisplayName("Creating expert group with valid parameters containing a picture, should be successful")
    void testCreateExpertGroup_validParamsWithout_successful() throws IOException {
        UUID currentUserId = UUID.randomUUID();
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        String link = "https://link/to/uploaded/file";
        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            picture,
            "http://www.example.com",
            currentUserId);

        long expectedId = new Random().nextLong();
        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(expectedId);
        doNothing().when(createExpertGroupAccessPort).persist(any(CreateExpertGroupAccessPort.Param.class));
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));
        when(uploadExpertGroupPicturePort.uploadPicture(picture)).thenReturn(link);

        var result = service.createExpertGroup(param);
        assertNotNull(result, """
            The result of createExpertGroup service
            should be CreateExpertGroupUseCase.Member
            """);
        assertEquals(expectedId, result.id(), "The result should be long ID");
    }

    @Test
    @DisplayName("Creating expert group with valid parameters without any picture, should be successful")
    void testCreateExpertGroup_validParams_persistResult() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            null,
            "http://www.example.com",
            currentUserId);

        long expectedId = new Random().nextLong();
        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(expectedId);
        doNothing().when(createExpertGroupAccessPort).persist(any(CreateExpertGroupAccessPort.Param.class));

        var result = service.createExpertGroup(param);
        assertNotNull(result, """
            The result of createExpertGroup service
            should be CreateExpertGroupUseCase.Member
            """);
        assertEquals(expectedId, result.id(), "The result should be long ID");
    }

    @Test
    void testCreateExpertGroup_expertGroupPersistProblem_transactionRollback() {
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            null,
            "http://www.example.com",
            currentUserId);

        when(createExpertGroupPort.persist(any(CreateExpertGroupPort.Param.class))).thenReturn(new Random().nextLong());
        doThrow(new RuntimeException("Simulated exception"))
            .when(createExpertGroupAccessPort).persist(any(CreateExpertGroupAccessPort.Param.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createExpertGroup(param));
        assertNotNull(exception);

        verify(createExpertGroupPort, times(1)).persist(any(CreateExpertGroupPort.Param.class));
    }

    @Test
    @DisplayName("The file size should be under the predefined circumstance")
    void testCreateExpertGroup_invalidPictureFileSize_throwException() {
        MockMultipartFile picture = new MockMultipartFile("pic", "pic.png", "text/plain", "some file".getBytes());

        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            picture,
            "http://www.example.com",
            UUID.randomUUID());
        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofBytes(1));
        var throwable = assertThrows(ValidationException.class, () -> service.createExpertGroup(param));
        assertEquals(UPLOAD_FILE_PICTURE_SIZE_MAX, throwable.getMessageKey());
    }

    @Test
    @DisplayName("The file content should be under the predefined circumstance")
    void testCreateExpertGroup_invalidPictureFileContent_throwException() {
        MockMultipartFile picture = new MockMultipartFile("pic", "pic.png", "application/zip", "some file".getBytes());
        Param param = new Param("Expert Group Name",
            "Expert Group Bio",
            "Expert Group About",
            picture,
            "http://www.example.com",
            UUID.randomUUID());

        when(fileProperties.getPictureMaxSize()).thenReturn(DataSize.ofMegabytes(5));
        when(fileProperties.getPictureContentTypes()).thenReturn(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp"));

        var throwable = assertThrows(ValidationException.class, () -> service.createExpertGroup(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());
    }
}
