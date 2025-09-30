package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnswerOptionsConvertTest {

    @Test
    void tesAnswerOptionsConvert() throws IOException {
        var answerOptionsSheet = createWorkbook().getSheet("AnswerOptions");
        var answerOptions = AnswerOptionsConverter.convert(answerOptionsSheet);

        assertEquals(2, answerOptions.size());

        // 2: YesNo
        List<AnswerOptionDslModel> noYes = answerOptions.get("YesNo");
        assertEquals(2, noYes.size());

        AnswerOptionDslModel option = noYes.getFirst();
        assertEquals(1, option.getIndex());
        assertEquals("No", option.getCaption());
        assertEquals(0.0, option.getValue());

        option = noYes.get(1);
        assertEquals(2, option.getIndex());
        assertEquals("Yes", option.getCaption());
        assertEquals(1, option.getValue());

        // 1: UsageRange
        List<AnswerOptionDslModel> usageRange = answerOptions.get("UsageRange");
        assertEquals(3, usageRange.size());

        option = usageRange.getFirst();
        assertEquals(1, option.getIndex());
        assertEquals("Never", option.getCaption());
        assertEquals(0.0, option.getValue());

        option = usageRange.get(1);
        assertEquals(2, option.getIndex());
        assertEquals("Often", option.getCaption());
        assertEquals(0.8, option.getValue());

        option = usageRange.get(2);
        assertEquals(3, option.getIndex());
        assertEquals("Always", option.getCaption());
        assertEquals(1, option.getValue());
    }

    private Workbook createWorkbook() throws IOException {
        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }
}
