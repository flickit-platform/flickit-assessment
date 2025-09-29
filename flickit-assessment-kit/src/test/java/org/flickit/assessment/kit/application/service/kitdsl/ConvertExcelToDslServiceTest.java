package org.flickit.assessment.kit.application.service.kitdsl;

import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.in.kitdsl.ConvertExcelToDslUseCase;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_SIZE_MAX;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConvertExcelToDslServiceTest {

    @InjectMocks
    private ConvertExcelToDslService service;

    @Mock
    private CreateKitDslPort createKitDslPort;

    @Spy
    private FileProperties fileProperties = fileProperties();

    private ConvertExcelToDslUseCase.Param param = createParam(ConvertExcelToDslUseCase.Param.ParamBuilder::build);
    private final String xlsxContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Test
    void convertExcelToDslService_whenExcelFileSizeIsLarge_thenThrowValidationException() {
        var file = new MockMultipartFile("file", "test.xlsx", xlsxContentType, new byte[6 * 1024 * 1024]);
        param = createParam(b -> b.excelFile(file));

        var throwable = assertThrows(ValidationException.class, () -> service.convertExcelToDsl(param));
        assertEquals(UPLOAD_FILE_SIZE_MAX, throwable.getMessageKey());

        verify(fileProperties, times(1)).getExcelKitMaxSize();
        verifyNoInteractions(createKitDslPort);
    }

    @Test
    void convertExcelToDslService_whenExcelFileFormatIsNotCorrect_thenThrowValidationException() {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[3 * 1024 * 124]);
        param = createParam(b -> b.excelFile(file));

        var throwable = assertThrows(ValidationException.class, () -> service.convertExcelToDsl(param));
        assertEquals(UPLOAD_FILE_FORMAT_NOT_VALID, throwable.getMessageKey());

        verify(fileProperties, times(1)).getExcelKitContentType();
        verify(fileProperties, times(1)).getExcelKitMaxSize();
        verifyNoInteractions(createKitDslPort);
    }

    @Test
    void convertExcelToDslService_whenParametersAreValid_thenConvertExcelToDslSuccess(){
        var file = new MockMultipartFile("file", "test.txt", xlsxContentType, new byte[3 * 1024 * 124]);
        param = createParam(b -> b.excelFile(file));

        service.convertExcelToDsl(param);

        verify(fileProperties, times(1)).getExcelKitContentType();
        verify(fileProperties, times(1)).getExcelKitMaxSize();
        verify(createKitDslPort).convert(param.getExcelFile());
    }

    FileProperties fileProperties() {
        var fileProperties = new FileProperties();
        fileProperties.setExcelKitContentType(xlsxContentType);
        fileProperties.setDslMaxSize(DataSize.ofMegabytes(5));
        return fileProperties;
    }

    private ConvertExcelToDslUseCase.Param createParam(Consumer<ConvertExcelToDslUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private ConvertExcelToDslUseCase.Param.ParamBuilder paramBuilder() {
        return ConvertExcelToDslUseCase.Param.builder()
            .excelFile(new MockMultipartFile("file", "test.txt", xlsxContentType, new byte[3 * 1024 * 124]))
            .currentUserId(UUID.randomUUID());
    }
}
