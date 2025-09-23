package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import org.apache.poi.ss.usermodel.*;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelToDslModelConverter {

    private static final String SHEET_QUALITY_ATTRIBUTES = "QualityAttributes";
    private static final String SUBJECT_NAME = "Subject Name";
    private static final String SUBJECT_TITLE = "Subject Title";
    private static final String SUBJECT_WEIGHT = "Subject Weight";
    private static final String SUBJECT_DESCRIPTION = "Subject Description";

    private static final String ATTRIBUTE_NAME = "Attribute Name";
    private static final String ATTRIBUTE_TITLE = "Attribute Title";
    private static final String ATTRIBUTE_WEIGHT = "Attribute Weight";
    private static final String ATTRIBUTE_DESCRIPTION = "Attribute Description";

    private static final String SHEET_QUESTIONNAIRES = "Questionnaires";
    private static final String QUESTIONNAIRE_NAME = "Name";
    private static final String QUESTIONNAIRE_TITLE = "Title";
    private static final String QUESTIONNAIRE_DESCRIPTION = "Description";

    private static final String SHEET_QUESTIONS = "Questions";
    private static final String QUESTION_QUESTION =  "Question";
    private static final String QUESTION_QUESTIONNAIRES = "Questionnaires";
    private static final String QUESTION_CODE = "Code";
    private static final String QUESTION_OPTIONS = "Options";
    private static final String QUESTION_DESCRIPTION = "Description";
    private static final String QUESTION_NOT_APPLICABLE = "NotApplicable";
    private static final String QUESTION_ADVISABLE = "Advisable";
    private static final String QUESTION_MATURITY = "Maturity";

    private static final String SHEET_MATURITY_LEVELS = "MaturityLevels";
    private static final String LEVEL_TITLE =  "Title";
    private static final String LEVEL_DESCRIPTION =  "Description";

    public static AssessmentKitDslModel convert(MultipartFile excelFile){
        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            var qualityAttributes = workbook.getSheet(SHEET_QUALITY_ATTRIBUTES);

            List<QuestionnaireDslModel> questionnaires = convertQuestionnaires(workbook.getSheet(SHEET_QUESTIONNAIRES));
            List<AttributeDslModel> attributes = convertAttributes(qualityAttributes);
            List<QuestionDslModel> questions = convertQuestions(workbook.getSheet(SHEET_QUESTIONS));
            List<SubjectDslModel> subjects = convertSubjects(qualityAttributes);

            return AssessmentKitDslModel.builder()
                .questionnaires(questionnaires)
                .attributes(attributes)
                .questions(questions)
                .subjects(subjects)
                .maturityLevels(null)
                .answerRanges(null)
                .hasError(false)
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<QuestionnaireDslModel> convertQuestionnaires(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        var columnMap = StreamSupport.stream(headerRow.spliterator(), false)
            .collect(Collectors.toMap(
                cell -> cell.getStringCellValue().trim(),
                Cell::getColumnIndex
            ));

        AtomicInteger index = new AtomicInteger(1);
        return IntStream.rangeClosed(1, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(Objects::nonNull)
            .flatMap(row -> {
                String code = getCellString(row, columnMap.get(QUESTIONNAIRE_NAME));
                if (code.isEmpty()) return Stream.empty(); // skip
                String title = getCellString(row, columnMap.get(QUESTIONNAIRE_TITLE));
                String description = getCellString(row, columnMap.get(QUESTIONNAIRE_DESCRIPTION));

                QuestionnaireDslModel questionnaireDslModel = QuestionnaireDslModel.builder()
                    .code(code)
                    .title(title)
                    .description(description)
                    .index(index.getAndIncrement())
                    .build();

                return Stream.of(questionnaireDslModel);
            })
            .toList();
    }

    static List<AttributeDslModel> convertAttributes(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        var columnMap = StreamSupport.stream(headerRow.spliterator(), false)
            .collect(Collectors.toMap(
                cell -> cell.getStringCellValue().trim(),
                Cell::getColumnIndex
            ));

        AtomicInteger index = new AtomicInteger(1);
        AtomicReference<String> currentSubjectCode = new AtomicReference<>("");

        return IntStream.rangeClosed(1, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(Objects::nonNull)
            .flatMap(row -> {
                String subjectCode = getCellString(row, columnMap.get(SUBJECT_NAME));
                if (subjectCode != null && !subjectCode.isBlank()) {
                    currentSubjectCode.set(subjectCode);
                }

                String attributeCode = getCellString(row, columnMap.get(ATTRIBUTE_NAME));
                if (attributeCode == null || attributeCode.isBlank()) {
                    return Stream.empty();
                }

                String title = getCellString(row, columnMap.get(ATTRIBUTE_TITLE));
                int weight = getCellInteger(row, columnMap.get(ATTRIBUTE_WEIGHT));
                String description = getCellString(row, columnMap.get(ATTRIBUTE_DESCRIPTION));

                AttributeDslModel attribute = AttributeDslModel.builder()
                    .subjectCode(currentSubjectCode.get())
                    .code(attributeCode)
                    .weight(weight)
                    .title(title)
                    .description(description)
                    .index(index.getAndIncrement())
                    .build();

                return Stream.of(attribute);
            })
            .toList();
    }

    static List<QuestionDslModel> convertQuestions(Sheet sheet) {
        Row headerRow = sheet.getRow(1);
        var columnMap = StreamSupport.stream(headerRow.spliterator(), false)
            .collect(Collectors.toMap(
                cell -> getCellStringValue(cell).trim(),
                Cell::getColumnIndex
            ));

        AtomicInteger index = new AtomicInteger(1);
        return IntStream.rangeClosed(1, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(Objects::nonNull)
            .flatMap(row -> {
                String title = getCellString(row, columnMap.get(QUESTION_QUESTION));
                if (title.isEmpty()) return Stream.empty(); // skip
                String questionnairesCode = getCellString(row, columnMap.get(QUESTION_QUESTIONNAIRES));
                String description = getCellString(row, columnMap.get(QUESTION_DESCRIPTION));
                String options = getCellString(row, columnMap.get(QUESTION_OPTIONS));
                String code = getCellString(row, columnMap.get(QUESTION_CODE));

                QuestionDslModel questionnaireDslModel = QuestionDslModel.builder()
                    .title(title)
                    .questionnaireCode(questionnairesCode)
                    .code(code)
                    .description(description)
                    .index(index.getAndIncrement())
                    .build();

                return Stream.of(questionnaireDslModel);
            })
            .toList();
    }

    static List<SubjectDslModel> convertSubjects(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        var columnMap = StreamSupport.stream(headerRow.spliterator(), false)
            .collect(Collectors.toMap(
                cell -> cell.getStringCellValue().trim(),
                Cell::getColumnIndex
            ));

        AtomicInteger index = new AtomicInteger(1);
        return IntStream.rangeClosed(1, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(Objects::nonNull)
            .flatMap(row -> {
                String code = getCellString(row, columnMap.get(SUBJECT_NAME));
                if (code.isEmpty()) return Stream.empty(); // skip
                String title = getCellString(row, columnMap.get(SUBJECT_TITLE));
                int weight = getCellInteger(row, columnMap.get(SUBJECT_WEIGHT));
                String description = getCellString(row, columnMap.get(SUBJECT_DESCRIPTION));

                SubjectDslModel subject = SubjectDslModel.builder()
                    .code(code)
                    .weight(weight)
                    .title(title)
                    .description(description)
                    .index(index.getAndIncrement())
                    .build();

                return Stream.of(subject);
            })
            .toList();
    }

    List<MaturityLevelDslModel> convertMaturityLevels(Sheet sheet) {
        Row headerRow = sheet.getRow(2);
        var columnMap = StreamSupport.stream(headerRow.spliterator(), false)
            .collect(Collectors.toMap(
                cell -> cell.getStringCellValue().trim(),
                Cell::getColumnIndex
            ));

        List<String> levels = IntStream.rangeClosed(2, sheet.getLastRowNum()) // از سطر سوم
            .mapToObj(sheet::getRow) // هر سطر رو بگیر
            .filter(Objects::nonNull) // سطر null نباشه
            .map(row -> row.getCell(0)) // ستون اول
            .filter(Objects::nonNull) // سلول null نباشه
            .map(cell -> cell.toString().trim()) // تبدیل به رشته
            .filter(s -> !s.isEmpty()) // خالی نباشه
            .toList();

        Map<String, Integer> competence = new HashMap<>();

        AtomicInteger index = new AtomicInteger(1);
        return IntStream.rangeClosed(2, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(Objects::nonNull)
            .flatMap(row -> {
                String code = row.getCell(0).toString();
                if (code.isEmpty()) return Stream.empty(); // skip
                String title = getCellString(row, columnMap.get(QUESTIONNAIRE_TITLE));
                String description = getCellString(row, columnMap.get(QUESTIONNAIRE_DESCRIPTION));

                MaturityLevelDslModel subject = MaturityLevelDslModel.builder()
                    .title(title)
                    .description(description)
                    .index(index.getAndIncrement())
                    .build();

                return Stream.of(subject);
            })
            .toList();
    }


    private static String getCellString(Row row, Integer idx) {
        if (idx == null || idx < 0) return null;
        Cell cell = row.getCell(idx);
        return (cell != null) ? cell.toString().trim() : null;
    }

    private static Integer getCellInteger(Row row, Integer idx) {
        if (idx == null || idx < 0) return null;
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try { return Integer.parseInt(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { return null; }
            default:
                return null;
        }
    }


    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return String.valueOf(cell.getNumericCellValue());
                    default:
                        return "";
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
            default:
                return "";
        }
    }

}
