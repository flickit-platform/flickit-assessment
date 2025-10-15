package org.flickit.assessment.kit.adapter.out.excel.dsl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.adapter.out.excel.dsl.converter.*;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertExcelToDslModelPort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.flickit.assessment.kit.common.ErrorMessageKey.CONVERT_EXCEL_TO_DSL_EXCEL_FILE_INVALID;

@Component
@RequiredArgsConstructor
public class ConvertExcelToDslModelAdapter implements ConvertExcelToDslModelPort {

    private static final String SHEET_QUALITY_ATTRIBUTES = "QualityAttributes";
    private static final String SHEET_QUESTIONNAIRES = "Questionnaires";
    private static final String SHEET_QUESTIONS = "Questions";
    private static final String SHEET_MATURITY_LEVELS = "MaturityLevels";
    private static final String SHEET_ANSWER_OPTIONS = "AnswerOptions";

    public AssessmentKitDslModel convert(MultipartFile excelFile) {
        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            var formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            var maturityLevelsSheet = workbook.getSheet(SHEET_MATURITY_LEVELS);
            var answerOptionsSheet = workbook.getSheet(SHEET_ANSWER_OPTIONS);
            var qualityAttributesSheet = workbook.getSheet(SHEET_QUALITY_ATTRIBUTES);
            var questionnairesSheet = workbook.getSheet(SHEET_QUESTIONNAIRES);
            var questionsSheet = workbook.getSheet(SHEET_QUESTIONS);

            var levels = MaturityLevelsConverter.convert(maturityLevelsSheet);
            var answerRanges = AnswerRangeConverter.convert(answerOptionsSheet);
            var subjects = QualityAttributesConverter.convertSubjects(qualityAttributesSheet);
            var attributes = QualityAttributesConverter.convertAttributes(qualityAttributesSheet);
            var questionnaires = QuestionnairesConverter.convert(questionnairesSheet);
            var questions = QuestionsConverter.convert(
                questionsSheet,
                formulaEvaluator,
                answerRanges,
                levels,
                attributes);

            return AssessmentKitDslModel.builder()
                .questionnaires(questionnaires)
                .attributes(attributes)
                .questions(questions)
                .subjects(subjects)
                .maturityLevels(levels)
                .answerRanges(answerRanges)
                .hasError(false)
                .build();
        } catch (IOException e) {
            throw new ValidationException(CONVERT_EXCEL_TO_DSL_EXCEL_FILE_INVALID);
        }
    }
}
