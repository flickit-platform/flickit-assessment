package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase;
import org.flickit.assessment.core.application.port.out.attributevalue.GenerateAttributeValueReportFilePort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoreExcelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Duration;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAttributeValueReportFileService implements CreateAttributeValueReportFileUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private static final String REPORT_FILE_NAME = "Attribute-report";

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final GenerateAttributeValueReportFilePort generateAttributeValueReportFilePort;
    private final UploadAttributeScoreExcelPort uploadAttributeScoreExcelPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result createAttributeValueReportFile(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        validateAssessmentResultPort.validate(param.getAssessmentId());

        InputStream inputStream = generateAttributeValueReportFilePort.generateReport(param.getAssessmentId(), param.getAttributeId());

        String filePath = uploadAttributeScoreExcelPort.uploadExcel(inputStream, REPORT_FILE_NAME);
        String downloadLink = createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
        return new Result(downloadLink);
    }
}
