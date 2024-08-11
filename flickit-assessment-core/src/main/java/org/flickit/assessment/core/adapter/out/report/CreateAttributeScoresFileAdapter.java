package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CreateAttributeScoresFileAdapter implements CreateAttributeScoresFilePort {

    private static final String QUESTIONS_SHEET_TITLE = "Questions";
    private static final String ATTRIBUTE_SHEET_TITLE = "Attribute";
    private static final String MATURITY_LEVELS_SHEET_TITLE = "MaturityLevels";
    private static final List<String> QUESTIONS_SHEET_HEADERS = List.of("Question", "Hint", "Weight", "Score");
    private static final List<String> ATTRIBUTE_SHEET_HEADERS = List.of("Attribute Title", "Attribute Maturity Level");
    private static final List<String> MATURITY_LEVELS_HEADERS = List.of("Title", "Index", "Description");

    @SneakyThrows
    @Override
    public Result generateFile(AttributeValue attributeValue, List<MaturityLevel> maturityLevels) {
        Workbook workbook = new XSSFWorkbook();
        createQuestionsSheet(attributeValue, workbook);
        createAttributeSheet(attributeValue, workbook);
        createMaturityLevelsSheet(maturityLevels, workbook);
        var stream = convertWorkbookToInputStream(workbook);
        return new Result(stream, convertToText(stream));
    }

    private void createQuestionsSheet(AttributeValue attributeValue, Workbook workbook) {
        Sheet sheet = initQuestionsSheet(workbook);
        createQuestionRows(attributeValue, workbook, sheet);
    }

    private void createAttributeSheet(AttributeValue attributeValue, Workbook workbook) {
        Sheet sheet = initAttributeSheet(workbook);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        createAttributeRow(attributeValue, sheet, style);
    }

    private void createMaturityLevelsSheet(List<MaturityLevel> maturityLevels, Workbook workbook) {
        Sheet sheet = initMaturityLevelsSheet(workbook);
        createMaturityLevelRows(maturityLevels, workbook, sheet);
    }

    private Sheet initQuestionsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(QUESTIONS_SHEET_TITLE);
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 2000);
        createHeader(QUESTIONS_SHEET_HEADERS, workbook, sheet);
        return sheet;
    }

    private Sheet initAttributeSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(ATTRIBUTE_SHEET_TITLE);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        createHeader(ATTRIBUTE_SHEET_HEADERS, workbook, sheet);
        return sheet;
    }

    private Sheet initMaturityLevelsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(MATURITY_LEVELS_SHEET_TITLE);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 10000);
        createHeader(MATURITY_LEVELS_HEADERS, workbook, sheet);
        return sheet;
    }

    private void createHeader(List<String> headers, Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(headers.get(i));
            headerCell.setCellStyle(headerStyle);
        }
    }

    private void createQuestionRows(AttributeValue attributeValue, Workbook workbook, Sheet sheet) {
        var attribute = attributeValue.getAttribute();
        var questions = attribute.getQuestions();
        var answers = attributeValue.getAnswers();
        if (questions == null || questions.isEmpty())
            return;
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        var questionIdToAnswerMap = answers.stream()
            .filter(a -> Boolean.TRUE.equals(a.getIsNotApplicable()) || a.getSelectedOption() != null)
            .collect(Collectors.toMap(Answer::getQuestionId, Function.identity()));

        int rowNumber = 1;
        for (Question question : questions) {
            var answer = questionIdToAnswerMap.get(question.getId());
            if (answer != null && answer.getIsNotApplicable())
                continue;

            Row row = sheet.createRow(rowNumber++);
            createQuestionRow(attribute.getId(), question, answer, row, style);
        }
    }

    private void createQuestionRow(Long attributeId, Question question, Answer answer, Row row, CellStyle style) {
        Cell cell = row.createCell(0);
        cell.setCellValue(question.getTitle());
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(question.getHint());
        cell.setCellStyle(style);

        int weight = question.getImpacts().stream()
            .filter(qi -> qi.getAttributeId() == attributeId)
            .findFirst() // This isn't perfectly accurate. Use weighted weighting for a more accurate result.
            .map(QuestionImpact::getWeight)
            .orElse(0);

        cell = row.createCell(2);
        cell.setCellValue(weight);
        cell.setCellStyle(style);

        double score = 0;
        if (answer != null && answer.getSelectedOption() != null && answer.getSelectedOption().getImpacts() != null) {
            score = answer.getSelectedOption().getImpacts().stream()
                .filter(ai -> ai.getQuestionImpact().getAttributeId() == attributeId)
                .findFirst()
                .map(AnswerOptionImpact::getValue)// This isn't perfectly accurate. Use weighted scoring for a more accurate result.
                .orElse(0.0);
        }

        cell = row.createCell(3);
        cell.setCellValue(score);
        cell.setCellStyle(style);
    }

    private void createAttributeRow(AttributeValue attributeValue, Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(1);
        Cell cell = row.createCell(0);
        cell.setCellValue(attributeValue.getAttribute().getTitle());
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(attributeValue.getMaturityLevel().getTitle());
        cell.setCellStyle(style);
    }

    private void createMaturityLevelRows(List<MaturityLevel> maturityLevels, Workbook workbook, Sheet sheet) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        for (int i = 0; i < maturityLevels.size(); i++) {
            Row row = sheet.createRow(i + 1);
            createMaturityLevelRow(maturityLevels.get(i), style, row);
        }
    }

    private void createMaturityLevelRow(MaturityLevel maturityLevel, CellStyle style, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(maturityLevel.getTitle());
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(maturityLevel.getIndex());
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue(maturityLevel.getDescription());
        cell.setCellStyle(style);
    }

    public ByteArrayInputStream convertWorkbookToInputStream(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public String convertToText(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        StringBuilder textBuilder = new StringBuilder();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            textBuilder.append("Sheet: ").append(sheet.getSheetName()).append("\n");

            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    textBuilder.append(cellValue).append("\t");
                }
                textBuilder.append("\n");
            }
            textBuilder.append("\n");
        }

        workbook.close();
        return textBuilder.toString();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case FORMULA -> {
                return cell.getCellFormula();
            }
            default -> {
                return "";
            }
        }
    }
}
