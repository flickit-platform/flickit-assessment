package org.flickit.assessment.core.adapter.out.report;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CreateAttributeScoresFileAdapterTest {

    @InjectMocks
    private CreateAttributeScoresFileAdapter adapter;

    @Test
    void testCreateAttributeScoresFileAdapter_ValidParam_CreateFile() {
        var attributeId = 1563L;

        Answer answer = AnswerMother.fullScoreOnLevels23(attributeId);
        Question question = QuestionMother.withIdAndImpactsOnLevel23(answer.getQuestionId(), attributeId);
        Attribute attribute = AttributeMother.withIdAndQuestions(attributeId, List.of(question));
        AttributeValue attributeValue = AttributeValueMother.withAttributeAndAnswerAndLevelOne(attribute, List.of(answer));
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();

        var result = adapter.generateFile(attributeValue, maturityLevels);

        assertNotNull(result);
        assertNotNull(result.stream());
        assertNotNull(result.text());
    }

    @SneakyThrows
    @Test
    void testCreateAttributeScoresFileAdapter_ValidParam_FileStructureShouldNotBeChanged() {
        var attributeId = 1563L;

        Answer answer = AnswerMother.fullScoreOnLevels23(attributeId);
        Question question = QuestionMother.withIdAndImpactsOnLevel23(answer.getQuestionId(), attributeId);
        Attribute attribute = AttributeMother.withIdAndQuestions(attributeId, List.of(question));
        AttributeValue attributeValue = AttributeValueMother.withAttributeAndAnswerAndLevelOne(attribute, List.of(answer));
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();

        var result = adapter.generateFile(attributeValue, maturityLevels);
        Workbook workbook = WorkbookFactory.create(result.stream());

        assertEquals(3, workbook.getNumberOfSheets());

        Sheet questionsSheet = workbook.getSheetAt(0);
        assertEquals(1, questionsSheet.getLastRowNum());

        Row questionsHeaderRow = questionsSheet.getRow(0);
        assertEquals(4, questionsHeaderRow.getLastCellNum());
        assertEquals("Question", questionsHeaderRow.getCell(0).getStringCellValue());
        assertEquals("Hint", questionsHeaderRow.getCell(1).getStringCellValue());
        assertEquals("Weight", questionsHeaderRow.getCell(2).getStringCellValue());
        assertEquals("Score", questionsHeaderRow.getCell(3).getStringCellValue());

        Row questionsFirstRow = questionsSheet.getRow(1);
        assertEquals(question.getTitle(), questionsFirstRow.getCell(0).getStringCellValue());
        assertEquals(question.getHint(), questionsFirstRow.getCell(1).getStringCellValue());
        assertEquals(question.getImpacts().get(0).getWeight(), questionsFirstRow.getCell(2).getNumericCellValue());
        assertEquals(answer.getSelectedOption().getImpacts().get(0).getValue(), questionsFirstRow.getCell(3).getNumericCellValue());

        Sheet attributeSheet = workbook.getSheetAt(1);
        assertEquals(1, attributeSheet.getLastRowNum());

        Row attributeHeaderRow = attributeSheet.getRow(0);
        assertEquals(2, attributeHeaderRow.getLastCellNum());
        assertEquals("Attribute Title", attributeHeaderRow.getCell(0).getStringCellValue());
        assertEquals("Attribute Maturity Level", attributeHeaderRow.getCell(1).getStringCellValue());

        Row attributeValueRow = attributeSheet.getRow(1);
        assertEquals(attribute.getTitle(), attributeValueRow.getCell(0).getStringCellValue());
        assertEquals(attributeValue.getMaturityLevel().getTitle(), attributeValueRow.getCell(1).getStringCellValue());

        Sheet maturityLevelsSheet = workbook.getSheetAt(2);
        assertEquals(maturityLevels.size(), maturityLevelsSheet.getLastRowNum());

        Row maturityLevelsHeaderRow = maturityLevelsSheet.getRow(0);
        assertEquals(3, maturityLevelsHeaderRow.getLastCellNum());
        assertEquals("Title", maturityLevelsHeaderRow.getCell(0).getStringCellValue());
        assertEquals("Index", maturityLevelsHeaderRow.getCell(1).getStringCellValue());
        assertEquals("Description", maturityLevelsHeaderRow.getCell(2).getStringCellValue());

        Row maturityLevelsFirstRow = maturityLevelsSheet.getRow(1);
        assertEquals(maturityLevels.get(0).getTitle(), maturityLevelsFirstRow.getCell(0).getStringCellValue());
        assertEquals(maturityLevels.get(0).getIndex(), maturityLevelsFirstRow.getCell(1).getNumericCellValue());
        assertEquals(maturityLevels.get(0).getDescription(), maturityLevelsFirstRow.getCell(2).getStringCellValue());
    }
}
