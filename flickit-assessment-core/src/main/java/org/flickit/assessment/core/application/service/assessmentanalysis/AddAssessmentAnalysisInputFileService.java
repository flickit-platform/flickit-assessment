package org.flickit.assessment.core.application.service.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.AddAssessmentAnalysisInputFileUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.UpdateAssessmentAnalysisInputPathPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.core.application.port.out.minio.UploadAssessmentAnalysisInputFilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ANALYSIS_TYPE_ID_NOT_VALID;

@Service
@Transactional
@RequiredArgsConstructor
public class AddAssessmentAnalysisInputFileService implements AddAssessmentAnalysisInputFileUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final UploadAssessmentAnalysisInputFilePort uploadAssessmentAnalysisInputFilePort;
    private final LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;
    private final CreateAssessmentAnalysisPort createAssessmentAnalysisPort;
    private final DeleteFilePort deleteFilePort;
    private final UpdateAssessmentAnalysisInputPathPort updateAssessmentAnalysisInputPathPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result addAssessmentAnalysisInputFile(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.ADD_ASSESSMENT_ANALYSIS_INPUT_FILE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!AnalysisType.isValidId(param.getAnalysisType()))
            throw new ResourceNotFoundException(ANALYSIS_TYPE_ID_NOT_VALID);

        var analysisType = AnalysisType.valueOfById(param.getAnalysisType());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var inputPath = uploadAssessmentAnalysisInputFilePort.uploadAssessmentAnalysisInputFile(param.getInputFile());

        var assessmentAnalysis = loadAssessmentAnalysisPort.load(assessmentResult.getId(), param.getAnalysisType());
        if (assessmentAnalysis.isEmpty()) {
            createAssessmentAnalysisPort.persist(toCreateParam(assessmentResult.getId(), analysisType, inputPath));
        } else {
            String oldInputPath = assessmentAnalysis.get().getInputPath();
            deleteFilePort.deleteFile(oldInputPath);
            updateAssessmentAnalysisInputPathPort.update(assessmentAnalysis.get().getId(), inputPath);
        }

        var downloadLink = createFileDownloadLinkPort.createDownloadLink(inputPath, EXPIRY_DURATION);
        return new Result(downloadLink);
    }

    private CreateAssessmentAnalysisPort.Param toCreateParam(UUID assessmentResultId, AnalysisType analysisType, String inputPath) {
        return new CreateAssessmentAnalysisPort.Param(assessmentResultId, analysisType, inputPath);
    }
}
