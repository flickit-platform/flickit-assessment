package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.adapter.out.uploaddsl.exception.DSLHasSyntaxErrorException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UploadKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.GetDslContentPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UploadKitPort;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadKitServiceTest {

    public static final String ZIP_FILE_ADDR = "src/test/java/org/flickit/assessment/kit/correct-kit.zip";
    @InjectMocks
    private UploadKitService service;

    @Mock
    private UploadKitPort uploadKitPort;

    @Mock
    private GetDslContentPort getDslContentPort;

    @Mock
    private CreateAssessmentKitDslPort createAssessmentKitDslPort;

    @SneakyThrows
    @Test
    void testUploadKit_ValidKitFile_ValidResult() {
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
        String zipFilePath = "sample/zip/file/path";
        String jsonFilePath = "sample/json/file/path";
        when(uploadKitPort.upload(dslFile, json)).thenReturn(new UploadKitPort.Result(zipFilePath, jsonFilePath));

        long kitDslId = 1L;
        when(createAssessmentKitDslPort.create(new CreateAssessmentKitDslPort.Param(zipFilePath, jsonFilePath)))
            .thenReturn(kitDslId);

        when(getDslContentPort.getDslContent(dslFile)).thenReturn(kitDslModel);

        var param = new UploadKitUseCase.Param(dslFile);
        Long uploadedKitInformation = service.upload(param);

        assertEquals(kitDslId, uploadedKitInformation);
    }

    @SneakyThrows
    @Test
    void testUploadKit_InvalidKitFile_DslSyntaxError() {
        MultipartFile dslFile = convertZipFileToMultipartFile(ZIP_FILE_ADDR);

        String message = "Some custom syntax error.";
        when(getDslContentPort.getDslContent(dslFile)).thenThrow(new DSLHasSyntaxErrorException(message));

        var param = new UploadKitUseCase.Param(dslFile);

        var throwable = assertThrows(DSLHasSyntaxErrorException.class, () -> service.upload(param));
        assertThat(throwable).hasMessage(message);
    }

    public MultipartFile convertZipFileToMultipartFile(String zipFilePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // Append each entry content to the output stream
                outputStream.write(StreamUtils.copyToByteArray(zipFile.getInputStream(entry)));
            }

            byte[] fileBytes = outputStream.toByteArray();
            return new MockMultipartFile("dslFile", zipFilePath, null, fileBytes);
        }
    }


}
