package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.ParsDslFilePort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitDslToFileStoragePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadKitServiceTest {

    public static final String ZIP_FILE_ADDR = "src/test/java/org/flickit/assessment/kit/correct-kit.zip";
    @InjectMocks
    private UploadKitService service;

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
    void testUploadKit_ValidKitFile_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));

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
        when(uploadKitDslToFileStoragePort.upload(dslFile, json)).thenReturn(new UploadKitDslToFileStoragePort.Result(dslFilePath, jsonFilePath));

        long kitDslId = 1L;
        when(createKitDslPort.create(dslFilePath, jsonFilePath))
            .thenReturn(kitDslId);

        when(parsDslFilePort.parsToDslModel(dslFile)).thenReturn(kitDslModel);

        var param = new UploadKitUseCase.Param(dslFile, expertGroupId, currentUserId);
        Long resultKitDslId = service.upload(param);

        assertEquals(kitDslId, resultKitDslId);
    }

    @SneakyThrows
    @Test
    void testUploadKit_CurrentUserNotExpertGroupOwner_CurrentUserValidationFail() {
        UUID currentUserId = UUID.randomUUID();
        Long expertGroupId = 1L;
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(UUID.randomUUID()));
        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);

        var param = new UploadKitUseCase.Param(dslFile, expertGroupId, currentUserId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.upload(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    public static MultipartFile convertZipFileToMultipartFile(String dslFilePath) throws IOException {
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
