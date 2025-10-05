package org.flickit.assessment.kit.application.service.kitdsl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.kitdsl.ConvertExcelToDslUseCase;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertAssessmentKitDslModelPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertExcelToDslModelPort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_SIZE_MAX;

@Service
@RequiredArgsConstructor
public class ConvertExcelToDslService implements ConvertExcelToDslUseCase {

    private final FileProperties fileProperties;
    private final ConvertExcelToDslModelPort convertExcelToDslModelPort;
    private final ConvertAssessmentKitDslModelPort convertAssessmentKitDslModelPort;

    @SneakyThrows
    @Override
    public Result convertExcelToDsl(Param param) {
        validateFile(param.getExcelFile());
        var assessmentKitDslModel = convertExcelToDslModelPort.convert(param.getExcelFile());
        var fileNameToContent = convertAssessmentKitDslModelPort.toDsl(assessmentKitDslModel);

        Map<String, byte[]> inMemoryFiles = new HashMap<>();
        fileNameToContent.forEach((key, value) -> {
            byte[] contentBytes = value.getBytes(StandardCharsets.UTF_8);
            inMemoryFiles.put(key, contentBytes);
        });

        return new Result(createZip(inMemoryFiles), "dsl-test.zip");
    }

    private void validateFile(MultipartFile excelFile) {
        if (excelFile.getSize() >= fileProperties.getExcelKitMaxSize().toBytes())
            throw new ValidationException(UPLOAD_FILE_SIZE_MAX);

        if (!fileProperties.getExcelKitContentType().equals(excelFile.getContentType()))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);
    }

    public static byte[] createZip(Map<String, byte[]> inMemoryFiles) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : inMemoryFiles.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(entry.getValue());
                zipOut.closeEntry();
            }

            zipOut.finish();
            return baos.toByteArray();
        }
    }
}
