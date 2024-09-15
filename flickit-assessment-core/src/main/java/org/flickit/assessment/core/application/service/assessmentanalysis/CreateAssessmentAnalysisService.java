package org.flickit.assessment.core.application.service.assessmentanalysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentAiAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.minio.ReadAssessmentAnalysisFilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAnalysisService implements CreateAssessmentAnalysisUseCase {

    private final AssessmentPermissionChecker assessmentPermissionChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;
    private final ReadAssessmentAnalysisFilePort readAssessmentAnalysisFilePort;
    private final CreateAssessmentAiAnalysisPort createAssessmentAiAnalysisPort;
    private final CreateAssessmentAnalysisPort createAssessmentAnalysisPort;

    @SneakyThrows
    @Override
    public void createAiAnalysis(Param param) {
        if (!assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ASSESSMENT_ANALYSIS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!AnalysisType.isValidId(param.getType()))
            throw new ResourceNotFoundException(ANALYSIS_TYPE_ID_NOT_VALID);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId());
        if (assessmentResult.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_RESULT_NOT_FOUND);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentAnalysis = loadAssessmentAnalysisPort.loadAssessmentAnalysis(assessmentResult.get().getId(), param.getType());
        if (assessmentAnalysis.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ANALYSIS_NOT_FOUND);

        var stream = readAssessmentAnalysisFilePort.readFileContent(assessmentAnalysis.get().getInputPath());
        String fileContent = new String(stream.readAllBytes());

        var analysisType = AnalysisType.valueOfById(param.getType());
        var aiAnalysis = createAssessmentAiAnalysisPort.generateAssessmentAnalysis(assessmentResult.get().getAssessment().getTitle(), fileContent, analysisType);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(aiAnalysis);

        createAssessmentAnalysisPort.persist(toAssessmentAnalysis(assessmentAnalysis.get(), jsonString));
    }

    private AssessmentAnalysis toAssessmentAnalysis(AssessmentAnalysis assessmentAnalysis, String aiAnalysis) {
        return new AssessmentAnalysis(assessmentAnalysis.getId(),
            assessmentAnalysis.getAssessmentResultId(),
            assessmentAnalysis.getType(),
            aiAnalysis,
            null,
            LocalDateTime.now(),
            null,
            null);
    }
}
