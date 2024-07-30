package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueExcelUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoreExcelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_VALUE_EXCEL_ATTRIBUTE_VALUE_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAttributeValueExcelService implements CreateAttributeValueExcelUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private static final String QUESTIONS_SHEET_TITLE = "Questions";
    private static final String ATTRIBUTE_SHEET_TITLE = "Attribute";
    private static final String MATURITY_LEVELS_SHEET_TITLE = "MaturityLevels";
    private static final List<String> QUESTIONS_SHEET_HEADERS = List.of("Question", "Hint", "Weight", "Score");
    private static final List<String> ATTRIBUTE_SHEET_HEADERS = List.of("Attribute Title", "Attribute Maturity Level");
    private static final List<String> MATURITY_LEVELS_HEADERS = List.of("Title", "Index", "Description");

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final UploadAttributeScoreExcelPort uploadAttributeScoreExcelPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @SneakyThrows
    @Override
    public Result createAttributeValueExcel(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());
        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());
        AttributeValue attributeValue = findAttributeValueByAttributeId(param.getAttributeId(), assessmentResult);

        Workbook workbook = new XSSFWorkbook();
        createQuestionsSheet(workbook, param, attributeValue);
        createAttributeSheet(workbook, attributeValue.getId());
        createMaturityLevelsSheet(workbook, assessmentResult.getKitVersionId());
        ByteArrayInputStream byteArrayInputStream = convertWorkbookToInputStream(workbook);

        String filePath = uploadAttributeScoreExcelPort.uploadExcel(byteArrayInputStream, attributeValue.getAttribute().getTitle());
        String downloadLink = createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
        return new Result(downloadLink);
    }

    private AttributeValue findAttributeValueByAttributeId(Long attributeId, AssessmentResult assessmentResult) {
        return assessmentResult.getSubjectValues().stream()
            .flatMap(s -> s.getAttributeValues().stream())
            .filter(a -> a.getAttribute().getId() == attributeId)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_VALUE_EXCEL_ATTRIBUTE_VALUE_NOT_FOUND));
    }

    private void createQuestionsSheet(Workbook workbook, Param param, AttributeValue attributeValue) {
        Sheet sheet = initQuestionsSheet(workbook);
        List<Question> questions = attributeValue.getAttribute().getQuestions();
        List<Answer> answers = attributeValue.getAnswers();
        createQuestionRows(param.getAttributeId(), questions, answers, workbook, sheet);
    }

    private void createAttributeSheet(Workbook workbook, UUID attributeValueId) {
        AttributeValue attributeValue = loadAttributeValuePort.load(attributeValueId);

        Sheet sheet = initAttributeSheet(workbook);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        createAttributeRow(attributeValue, sheet, style);
    }

    private void createMaturityLevelsSheet(Workbook workbook, long kitVersionId) {
        Sheet sheet = initMaturityLevelsSheet(workbook);

        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(kitVersionId);
        createMaturityLevelRows(maturityLevels, workbook, sheet);
    }

    private Sheet initQuestionsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(QUESTIONS_SHEET_TITLE);
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 2000);
        createHeader(workbook, sheet, QUESTIONS_SHEET_HEADERS);
        return sheet;
    }

    private Sheet initAttributeSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(ATTRIBUTE_SHEET_TITLE);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        createHeader(workbook, sheet, ATTRIBUTE_SHEET_HEADERS);
        return sheet;
    }

    private Sheet initMaturityLevelsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(MATURITY_LEVELS_SHEET_TITLE);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 10000);
        createHeader(workbook, sheet, MATURITY_LEVELS_HEADERS);
        return sheet;
    }

    private void createHeader(Workbook workbook, Sheet sheet, List<String> headers) {
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

    private void createQuestionRows(Long attributeId, List<Question> questions, List<Answer> answers, Workbook workbook, Sheet sheet) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        Map<Long, Answer> questionIdToAnswerMap = answers.stream()
            .filter(a -> Boolean.TRUE.equals(a.getIsNotApplicable()) || a.getSelectedOption() != null)
            .collect(Collectors.toMap(Answer::getQuestionId, Function.identity()));

        int rowNumber = 1;
        for (Question question : questions) {
            Answer answer = questionIdToAnswerMap.get(question.getId());
            if (answer != null && answer.getIsNotApplicable())
                continue;

            Row row = sheet.createRow(rowNumber++);
            createQuestionRow(attributeId, style, row, question, answer);
        }
    }

    private void createQuestionRow(Long attributeId, CellStyle style, Row row, Question question, Answer answer) {
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
        if (answer != null) {
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
    }

    public ByteArrayInputStream convertWorkbookToInputStream(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
