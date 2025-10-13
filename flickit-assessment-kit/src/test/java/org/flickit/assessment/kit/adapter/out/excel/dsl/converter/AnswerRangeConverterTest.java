package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnswerRangeConverterTest {

    @Test
    void testAnswerRangeConvertor() throws IOException {
        var sheet = createWorkbook().getSheet("AnswerOptions");
        var answerRanges = AnswerRangeConverter.convert(sheet);

        assertEquals(2, answerRanges.size());

        // ---- Range 1: UsageRange ----
        AnswerRangeDslModel usageRange = answerRanges.stream()
            .filter(r -> "UsageRange".equals(r.getCode()))
            .findFirst()
            .orElseThrow();

        List<AnswerOptionDslModel> usageOptions = usageRange.getAnswerOptions();
        assertEquals(3, usageOptions.size());

        var option = usageOptions.getFirst();
        assertEquals(1, option.getIndex());
        assertEquals("Never", option.getCaption());
        assertEquals(0.0, option.getValue());

        option = usageOptions.get(1);
        assertEquals(2, option.getIndex());
        assertEquals("Often", option.getCaption());
        assertEquals(0.8, option.getValue());

        option = usageOptions.get(2);
        assertEquals(3, option.getIndex());
        assertEquals("Always", option.getCaption());
        assertEquals(1.0, option.getValue());

        // ---- Range 2: YesNo ----
        AnswerRangeDslModel yesNoRange = answerRanges.stream()
            .filter(r -> "YesNo".equals(r.getCode()))
            .findFirst()
            .orElseThrow();

        List<AnswerOptionDslModel> yesNoOptions = yesNoRange.getAnswerOptions();
        assertEquals(2, yesNoOptions.size());

        option = yesNoOptions.getFirst();
        assertEquals(1, option.getIndex());
        assertEquals("No", option.getCaption());
        assertEquals(0.0, option.getValue());

        option = yesNoOptions.get(1);
        assertEquals(2, option.getIndex());
        assertEquals("Yes", option.getCaption());
        assertEquals(1.0, option.getValue());
    }

    private Workbook createWorkbook() throws IOException {
        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }
}
