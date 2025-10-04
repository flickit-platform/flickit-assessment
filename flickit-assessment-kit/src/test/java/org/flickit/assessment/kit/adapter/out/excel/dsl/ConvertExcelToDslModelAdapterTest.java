package org.flickit.assessment.kit.adapter.out.excel.dsl;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
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
import java.util.List;

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

        assertQuestions(dslModel.getQuestions());
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

    private static void assertQuestions(List<QuestionDslModel> questions) {
        //Asser Questions
        assertEquals(5, questions.size());
        //Assert Question 1
        QuestionDslModel question = questions.getFirst();
        assertEquals(1, question.getIndex());
        assertEquals("Question Development 1", question.getTitle());
        assertEquals("Development", question.getQuestionnaireCode());
        assertEquals("Q1", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question Development 1 Description", question.getDescription());
        assertFalse(question.isMayNotBeApplicable());
        assertTrue(question.isAdvisable());
        //Assert Question 2
        question = questions.get(1);
        assertEquals(2, question.getIndex());
        assertEquals("Question Development 2", question.getTitle());
        assertEquals("Development", question.getQuestionnaireCode());
        assertEquals("Q2", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question Development 2 Description", question.getDescription());
        assertFalse(question.isMayNotBeApplicable());
        assertTrue(question.isAdvisable());
        //Assert Question 3
        question = questions.get(2);
        assertEquals(3, question.getIndex());
        assertEquals("Question DevOps 1", question.getTitle());
        assertEquals("DevOps", question.getQuestionnaireCode());
        assertEquals("Q13", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question DevOps 1 Description", question.getDescription());
        assertTrue(question.isMayNotBeApplicable());
        assertTrue(question.isAdvisable());
        //Assert Question 4
        question = questions.get(3);
        assertEquals(4, question.getIndex());
        assertEquals("Question TeamCollaboration 1", question.getTitle());
        assertEquals("TeamCollaboration", question.getQuestionnaireCode());
        assertEquals("Q20", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question TeamCollaboration 1 Description", question.getDescription());
        assertFalse(question.isMayNotBeApplicable());
        assertFalse(question.isAdvisable());
        //Assert Question 5
        question = questions.get(4);
        assertEquals(5, question.getIndex());
        assertEquals("Question TeamCollaboration 2", question.getTitle());
        assertEquals("TeamCollaboration", question.getQuestionnaireCode());
        assertEquals("Q22", question.getCode());
        assertEquals("UsageRange", question.getAnswerRangeCode());
        assertEquals("Question TeamCollaboration 2 Description", question.getDescription());
        assertTrue(question.isMayNotBeApplicable());
        assertFalse(question.isAdvisable());
    }
}
