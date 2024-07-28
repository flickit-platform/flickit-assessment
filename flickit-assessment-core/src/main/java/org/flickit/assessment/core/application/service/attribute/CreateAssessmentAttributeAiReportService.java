package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.minio.DownloadFilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_AI_ANALYSIS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportService implements CreateAssessmentAttributeAiReportUseCase {

    private final FileProperties fileProperties;
    private final DownloadFilePort downloadFilePort;
    private final LoadAttributePort loadAttributePort;
    private final GetAssessmentPort getAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @SneakyThrows
    @Override
    public Result create(Param param) {
        String extension = getFileExtension(param.getFileLink());
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        if (!assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!fileProperties.getAttributeReportFileExtension().contains(extension))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());

        var stream = downloadFilePort.downloadFile(param.getFileLink());
        return new Result(createAssessmentAttributeAiPort.createReport(stream, attribute));
    }

    public String getFileExtension(String fileLink) {
        int lastIndexOfDot = fileLink.lastIndexOf('.');
        int firstIndexOfQuestionMark = fileLink.indexOf('?', lastIndexOfDot);
        return fileLink.substring(lastIndexOfDot + 1, firstIndexOfQuestionMark);
    }
}
