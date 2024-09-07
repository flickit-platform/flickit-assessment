package org.flickit.assessment.core.application.service.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentAiAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.minio.ReadAssessmentAnalysisFilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ANALYSIS_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessmentAnalysisService implements CreateAssessmentAnalysisUseCase {

    private final AssessmentPermissionChecker assessmentPermissionChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResult validateAssessmentResult;
    private final LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;
    private final ReadAssessmentAnalysisFilePort readAssessmentAnalysisFilePort;
    private final CreateAssessmentAnalysisPort createAssessmentAnalysisPort;
    private final CreateAssessmentAiAnalysisPort createAssessmentAiAnalysisPort;

    @SneakyThrows
    @Override
    public void createAiAnalysis(Param param) {
        if (!assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ASSESSMENT_AI_ANALYSIS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId());
        if (assessmentResult.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_RESULT_NOT_FOUND);

        validateAssessmentResult.validate(param.getAssessmentId());

        var assessmentAnalysis = loadAssessmentAnalysisPort.loadAssessmentAnalysis(assessmentResult.get().getId(), param.getType());
        if (assessmentAnalysis.isEmpty())
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_AI_ANALYSIS_ASSESSMENT_ANALYSIS_NOT_FOUND);

        var stream = readAssessmentAnalysisFilePort.readFileContent(assessmentAnalysis.get().getInputPath());
        String fileContent = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

        String aiAnalysis = createAssessmentAiAnalysisPort.generateAssessmentAnalysis(fileContent);

        createAssessmentAnalysisPort.create(toAssessmentAnalysis(assessmentAnalysis.get(), aiAnalysis));
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
