package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAttributeScoresFilePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.UploadAttributeScoreExcelPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAttributeValueReportFileService implements CreateAttributeValueReportFileUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private static final String REPORT_FILE_NAME = "attribute-report.xlsx";

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final CreateAttributeScoresFilePort createAttributeScoresFilePort;
    private final UploadAttributeScoreExcelPort uploadAttributeScoreExcelPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result createAttributeValueReportFile(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        validateAssessmentResultPort.validate(param.getAssessmentId());

        AssessmentResult assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        AttributeValue attributeValue = loadAttributeValuePort.load(assessmentResult.getId(), param.getAttributeId());
        List<MaturityLevel> maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());
        InputStream inputStream = createAttributeScoresFilePort.generateFile(attributeValue, maturityLevels);

        String filePath = uploadAttributeScoreExcelPort.uploadExcel(inputStream, REPORT_FILE_NAME);
        String downloadLink = createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
        return new Result(downloadLink);
    }
}
