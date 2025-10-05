package org.flickit.assessment.kit.adapter.out.excel.dsl;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConvertExcelToDslModelAdapterTest {

    @InjectMocks
    ConvertExcelToDslModelAdapter adapter;

    @Test
    void testExcelToDslModelConverterTest_testConvertSubjects() throws IOException {
        final Workbook workbook = createWorkbook();

        MultipartFile multipartFile = toMultipartFile(workbook);

        var dslModel = adapter.convert(multipartFile);

        assertNotNull(dslModel);
        assertNotNull(dslModel.getSubjects());
        assertNotNull(dslModel.getQuestions());
        assertNotNull(dslModel.getQuestionnaires());
        assertNotNull(dslModel.getAttributes());
        assertNotNull(dslModel.getAnswerRanges());
        assertNotNull(dslModel.getMaturityLevels());
        assertFalse(dslModel.isHasError());
    }

    private Workbook createWorkbook() throws IOException {

        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }

    public MultipartFile toMultipartFile(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            workbook.close();
            return new MockMultipartFile(
                "file",
                "fileName",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
            );
        }
    }
}
