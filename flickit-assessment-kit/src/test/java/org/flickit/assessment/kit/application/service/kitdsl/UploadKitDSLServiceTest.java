package org.flickit.assessment.kit.application.service.kitdsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.in.kitdsl.UploadKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.ParsDslFilePort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UploadKitDslToFileStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadKitDSLServiceTest {

    public static final String ZIP_FILE_ADDR = "src/test/resources/correct-kit.zip";

    @InjectMocks
    private UploadKitDslService service;

    @Mock
    FileProperties fileProperties;

    @Mock
    private UploadKitDslToFileStoragePort uploadKitDslToFileStoragePort;

    @Mock
    private ParsDslFilePort parsDslFilePort;

    @Mock
    private CreateKitDslPort createKitDslPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @SneakyThrows
    @Test
    void testUploadKitDsl_ValidKitFile_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofMegabytes(10));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);

        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);
        QuestionnaireDslModel q1 = QuestionnaireDslModel.builder().title("Clean Architecture").description("desc").build();
        QuestionnaireDslModel q2 = QuestionnaireDslModel.builder().title("Code Quality").description("desc").build();
        SubjectDslModel s1 = SubjectDslModel.builder().title("Software").description("desc").build();
        SubjectDslModel s2 = SubjectDslModel.builder().title("Team").description("desc").build();
        AssessmentKitDslModel kitDslModel = AssessmentKitDslModel.builder()
            .questionnaires(List.of(q1, q2))
            .subjects(List.of(s1, s2))
            .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(kitDslModel);
        String dslFilePath = "sample/zip/file/path";
        String jsonFilePath = "sample/json/file/path";
        when(uploadKitDslToFileStoragePort.uploadKitDsl(dslFile, json)).thenReturn(new UploadKitDslToFileStoragePort.Result(dslFilePath, jsonFilePath));

        long kitDslId = 1L;
        when(createKitDslPort.create(dslFilePath, jsonFilePath, currentUserId))
            .thenReturn(kitDslId);

        when(parsDslFilePort.parsToDslModel(dslFile)).thenReturn(kitDslModel);

        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);
        Long resultKitDslId = service.upload(param);

        assertEquals(kitDslId, resultKitDslId);
    }

    @SneakyThrows
    @Test
    void testUploadKit_CurrentUserNotExpertGroupOwner_CurrentUserValidationFail() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(UUID.randomUUID());
        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);

        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofMegabytes(10));

        var throwable = assertThrows(AccessDeniedException.class, () -> service.upload(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @SneakyThrows
    @Test
    void testUploadKit_InvalidFileSize_ThrowException() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;

        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);

        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofBytes(1));

        var throwable = assertThrows(ValidationException.class, () -> service.upload(param));
        assertEquals(UPLOAD_FILE_DSL_SIZE_MAX, throwable.getMessageKey());
    }

    @SneakyThrows
    @Test
    void testUploadKit_InvalidFileType_ThrowException() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;

        MockMultipartFile dslFile = new MockMultipartFile("pic", "pic.png", "application/png", "some file".getBytes());

        var param = new UploadKitDslUseCase.Param(dslFile, expertGroupId, currentUserId);

        when(fileProperties.getDslMaxSize()).thenReturn(DataSize.ofMegabytes(5));

        var throwable = assertThrows(ValidationException.class, () -> service.upload(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());
    }

    private static MultipartFile convertZipFileToMultipartFile(String dslFilePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(dslFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // Append each entry content to the output stream
                outputStream.write(StreamUtils.copyToByteArray(zipFile.getInputStream(entry)));
            }

            byte[] fileBytes = outputStream.toByteArray();
            return new MockMultipartFile("dslFile", dslFilePath, null, fileBytes);
        }
    }
}
