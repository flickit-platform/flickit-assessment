package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MaturityLevelsConverterTest {

    @Test
    void testMaturityLevelsConverter() throws IOException {
        var maturityLevelsSheet = createWorkbook().getSheet("MaturityLevels");
        var maturityLevels = MaturityLevelsConverter.convert(maturityLevelsSheet);

        assertEquals(4, maturityLevels.size());
        // 1: Unprepared
        MaturityLevelDslModel maturityLevel = maturityLevels.getFirst();
        assertEquals(1, maturityLevel.getIndex());
        assertEquals(1, maturityLevel.getValue());
        assertEquals("Unprepared", maturityLevel.getCode());
        assertEquals("Unprepared Title", maturityLevel.getTitle());
        assertEquals("Unprepared Description", maturityLevel.getDescription());
        assertNull(maturityLevel.getCompetencesCodeToValueMap());
        // 2: Prepared
        maturityLevel = maturityLevels.get(1);
        assertEquals(2, maturityLevel.getIndex());
        assertEquals(2, maturityLevel.getValue());
        assertEquals("Prepared", maturityLevel.getCode());
        assertEquals("Prepared Title", maturityLevel.getTitle());
        assertEquals("Prepared Description", maturityLevel.getDescription());
        assertEquals(1, maturityLevel.getCompetencesCodeToValueMap().size());
        assertEquals(70, maturityLevel.getCompetencesCodeToValueMap().get("Prepared"));
        // 3: WellEquipped
        maturityLevel = maturityLevels.get(2);
        assertEquals(3, maturityLevel.getIndex());
        assertEquals(3, maturityLevel.getValue());
        assertEquals("WellEquipped", maturityLevel.getCode());
        assertEquals("Well Equipped Title", maturityLevel.getTitle());
        assertEquals("Well Equipped Description", maturityLevel.getDescription());
        assertEquals(2, maturityLevel.getCompetencesCodeToValueMap().size());
        assertEquals(80, maturityLevel.getCompetencesCodeToValueMap().get("Prepared"));
        assertEquals(70, maturityLevel.getCompetencesCodeToValueMap().get("WellEquipped"));
        // 4: WellEquipped
        maturityLevel = maturityLevels.getLast();
        assertEquals(4, maturityLevel.getIndex());
        assertEquals(4, maturityLevel.getValue());
        assertEquals("StateOfTheArt", maturityLevel.getCode());
        assertEquals("State of the Art Title", maturityLevel.getTitle());
        assertEquals("State of the Art Description", maturityLevel.getDescription());
        assertEquals(3, maturityLevel.getCompetencesCodeToValueMap().size());
        assertEquals(80, maturityLevel.getCompetencesCodeToValueMap().get("Prepared"));
        assertEquals(80, maturityLevel.getCompetencesCodeToValueMap().get("WellEquipped"));
        assertEquals(40, maturityLevel.getCompetencesCodeToValueMap().get("StateOfTheArt"));
    }

    private Workbook createWorkbook() throws IOException {

        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }
}
