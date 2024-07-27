package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.FileProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.out.minio.DownloadFilePort;
import org.flickit.assessment.core.application.port.out.aireport.CreateAssessmentAttributeAiPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_AI_ANALYSIS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.UPLOAD_FILE_FORMAT_NOT_VALID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportService implements CreateAssessmentAttributeAiReportUseCase {

    private final FileProperties fileProperties;
    private final DownloadFilePort downloadFilePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @SneakyThrows
    @Override
    public Result create(Param param) {
        String extension = getFileExtension(param.getFileLink());
        if (!fileProperties.getAttributeReportFileExtension().contains(extension))
            throw new ValidationException(UPLOAD_FILE_FORMAT_NOT_VALID);

        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), CREATE_AI_ANALYSIS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String filePath = downloadFilePort.downloadFile(param.getFileLink());

        var file = new File(filePath);

        return new Result(createAssessmentAttributeAiPort.createReport(file));
    }

    public String getFileExtension(String fileLink) {
        int lastIndexOfDot = fileLink.lastIndexOf('.');
        int firstIndexOfQuestionMark = fileLink.indexOf('?', lastIndexOfDot);
        return fileLink.substring(lastIndexOfDot + 1, firstIndexOfQuestionMark);
    }
}
