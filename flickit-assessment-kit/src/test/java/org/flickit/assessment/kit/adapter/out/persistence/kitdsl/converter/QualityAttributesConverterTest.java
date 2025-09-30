package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QualityAttributesConverterTest {

    @Test
    void testQualityAttributesConverter() throws IOException {
        var qualityAttributesSheet = createWorkbook().getSheet("QualityAttributes");
        var subjectDslModels = QualityAttributesConverter.convertSubjects(qualityAttributesSheet);
        var attributeDslModels = QualityAttributesConverter.convertAttributes(qualityAttributesSheet);

        //Assert subject 1
        assertEquals(2, subjectDslModels.size());
        SubjectDslModel subject1 = subjectDslModels.getFirst();
        assertEquals("Team", subject1.getCode());
        assertEquals("Team Title", subject1.getTitle());
        assertEquals(2, subject1.getWeight());
        assertEquals("Subject Description 1", subject1.getDescription());
        assertEquals(1, subject1.getIndex());
        //Assert Attribute 1
        AttributeDslModel attr1 = attributeDslModels.getFirst();
        assertEquals("Team", attr1.getSubjectCode());
        assertEquals("AttributeOne", attr1.getCode());
        assertEquals("Attribute One", attr1.getTitle());
        assertEquals(1, attr1.getWeight());
        assertEquals("Attribute One Description", attr1.getDescription());
        assertEquals(1, attr1.getIndex());
        //Assert Attribute 2
        AttributeDslModel attr2 = attributeDslModels.get(1);
        assertEquals("Team", attr2.getSubjectCode());
        assertEquals("AttributeTwo", attr2.getCode());
        assertEquals("Attribute Two", attr2.getTitle());
        assertEquals(3, attr2.getWeight());
        assertEquals("Attribute Two Description", attr2.getDescription());
        assertEquals(2, attr2.getIndex());
        //Assert subject 2
        SubjectDslModel subject2 = subjectDslModels.getLast();
        assertEquals("Software", subject2.getCode());
        assertEquals("Software Title", subject2.getTitle());
        assertEquals(1, subject2.getWeight());
        assertEquals("Subject Description 2", subject2.getDescription());
        assertEquals(2, subject2.getIndex());
        //Assert Attribute 3
        AttributeDslModel attr3 = attributeDslModels.getLast();
        assertEquals("Software", attr3.getSubjectCode());
        assertEquals("AttributeThree", attr3.getCode());
        assertEquals("Attribute Three", attr3.getTitle());
        assertEquals(2, attr3.getWeight());
        assertEquals("Attribute Three Description", attr3.getDescription());
        assertEquals(3, attr3.getIndex());
    }

    private Workbook createWorkbook() throws IOException {

        File file = new File("src/test/resources/correct-excel-kit.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
            file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IOUtils.toByteArray(input));

        return new XSSFWorkbook(multipartFile.getInputStream());
    }
}
