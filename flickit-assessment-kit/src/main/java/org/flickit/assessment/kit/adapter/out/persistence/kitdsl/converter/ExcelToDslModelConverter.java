package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.flickit.assessment.kit.common.ErrorMessageKey.CONVERT_EXCEL_TO_DSL_EXCEL_FILE_INVALID;

@UtilityClass
public class ExcelToDslModelConverter {

    private static final String SHEET_QUALITY_ATTRIBUTES = "QualityAttributes";
    private static final String SHEET_QUESTIONNAIRES = "Questionnaires";
    private static final String SHEET_QUESTIONS = "Questions";
    private static final String SHEET_MATURITY_LEVELS = "MaturityLevels";
    private static final String SHEET_ANSWER_OPTIONS = "AnswerOptions";

    public static AssessmentKitDslModel convert(MultipartFile excelFile) {
        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            var qualityAttributes = workbook.getSheet(SHEET_QUALITY_ATTRIBUTES);

            var questionnaires = QuestionnairesConverter.convert(workbook.getSheet(SHEET_QUESTIONNAIRES));
            var attributes = QualityAttributesConverter.convertAttributes(qualityAttributes);
            var subjects = QualityAttributesConverter.convertSubjects(qualityAttributes);
            var answerRangeCodeToAnswerOptionsMap = AnswerOptionsConverter.convert(workbook.getSheet(SHEET_ANSWER_OPTIONS));
            var levels = MaturityLevelsConverter.convert(workbook.getSheet(SHEET_MATURITY_LEVELS));
            var maturityLevelsCodeToMaturityLevelDslModel = levels.stream()
                .collect(Collectors.toMap(MaturityLevelDslModel::getCode, Function.identity()));
            var questions = QuestionsConverter.convert(workbook.getSheet(SHEET_QUESTIONS), answerRangeCodeToAnswerOptionsMap, maturityLevelsCodeToMaturityLevelDslModel, attributes);

            return AssessmentKitDslModel.builder()
                .questionnaires(questionnaires)
                .attributes(attributes)
                .questions(questions)
                .subjects(subjects)
                .maturityLevels(levels)
                .hasError(false)
                .build();
        } catch (IOException e) {
            throw new ValidationException(CONVERT_EXCEL_TO_DSL_EXCEL_FILE_INVALID);
        }
    }

    static Map<String, Integer> getSheetHeader(Sheet sheet, int rowNum, int start, int end) {
        Row headerRow = sheet.getRow(rowNum);
        return StreamSupport.stream(headerRow.spliterator(), false)
            .skip(start)
            .limit((long) end - start + 1)
            .collect(Collectors.toMap(
                cell -> getCellStringValue(cell).trim(),
                Cell::getColumnIndex
            ));
    }

    static boolean isBlankRow(Row row) {
        if (row == null)
            return true;
        for (Cell cell : row)
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = cell.toString().trim();
                if (!value.isEmpty())
                    return false;
            }
        return true;
    }

    static String getCellString(Row row, Integer idx) {
        if (idx == null || idx < 0) return null;
        Cell cell = row.getCell(idx);
        return (cell != null) ? cell.toString().trim() : null;
    }

    static Integer getCellInteger(Row row, Integer idx) {
        if (idx == null || idx < 0) return null;
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    static Double getCellDouble(Row row, int columnIndex) {
        if (row == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (IllegalStateException e) {
                    return null;
                }
            case BLANK:
            default:
                return null;
        }
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case FORMULA -> switch (cell.getCachedFormulaResultType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                default -> "";
            };
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
