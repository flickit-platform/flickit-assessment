package org.flickit.assessment.core.application.service.assessmentanalysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAiAnalysisUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.UpdateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.minio.ReadAssessmentAnalysisFilePort;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAiAnalysisService implements CreateAssessmentAiAnalysisUseCase {

    private final AssessmentPermissionChecker assessmentPermissionChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;
    private final ReadAssessmentAnalysisFilePort readAssessmentAnalysisFilePort;
    private final UpdateAssessmentAnalysisPort updateAssessmentAnalysisPort;
    private final OpenAiProperties openAiProperties;
    private final CallAiPromptPort callAiPromptPort;

    @SneakyThrows
    @Override
    public void createAiAnalysis(Param param) {
        if (!assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ASSESSMENT_AI_ANALYSIS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!AnalysisType.isValidId(param.getType()))
            throw new ResourceNotFoundException(ANALYSIS_TYPE_ID_NOT_VALID);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId());
        if (assessmentResult.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_RESULT_NOT_FOUND);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentAnalysis = loadAssessmentAnalysisPort.load(assessmentResult.get().getId(), param.getType());
        if (assessmentAnalysis.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ANALYSIS_NOT_FOUND);

        var inputStream = readAssessmentAnalysisFilePort.readFileContent(assessmentAnalysis.get().getInputPath());
        Workbook workbook = WorkbookFactory.create(inputStream);
        String fileContent = convertWorkbookToText(workbook);

        var analysisType = AnalysisType.valueOfById(param.getType());
        BeanOutputConverter<AssessmentAnalysisInsight> converter = new BeanOutputConverter<>(AssessmentAnalysisInsight.class);
        var prompt = openAiProperties.createAssessmentAnalysisPrompt(assessmentResult.get().getAssessment().getTitle(), fileContent, analysisType.name(), converter.getFormat());
        String aiAnalysis = callAiPromptPort.call(prompt);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(aiAnalysis);

        updateAssessmentAnalysisPort.updateAiAnalysis(toAssessmentAnalysis(assessmentAnalysis.get(), jsonString));
    }

    public String convertWorkbookToText(Workbook workbook) throws IOException {
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

    private AssessmentAnalysis toAssessmentAnalysis(AssessmentAnalysis assessmentAnalysis, String aiAnalysis) {
        return new AssessmentAnalysis(assessmentAnalysis.getId(),
            assessmentAnalysis.getAssessmentResultId(),
            assessmentAnalysis.getType(),
            aiAnalysis,
            assessmentAnalysis.getAssessorAnalysis(),
            LocalDateTime.now(),
            assessmentAnalysis.getAssessorAnalysisTime(),
            assessmentAnalysis.getInputPath());
    }
}
