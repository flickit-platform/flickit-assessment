package org.flickit.assessment.kit.adapter.out.excel.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Collectors;

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
}
