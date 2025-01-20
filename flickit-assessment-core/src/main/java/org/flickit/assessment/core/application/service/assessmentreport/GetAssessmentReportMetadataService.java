package org.flickit.assessment.core.application.service.assessmentreport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportMetadataPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentReportMetadataService implements GetAssessmentReportMetadataUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentReportMetadataPort loadAssessmentReportMetadataPort;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Result getAssessmentReportMetadata(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadAssessmentReportMetadataPort.load(param.getAssessmentId());
        if (portResult != null && !portResult.isBlank()) {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return toResult(objectMapper.readValue(portResult, AssessmentReportMetadata.class));
        }

        return new Result(null, null, null, null);
    }

    private Result toResult(AssessmentReportMetadata metadata) {
        return new Result(metadata.intro(),
            metadata.prosAndCons(),
            metadata.steps(),
            metadata.participants());
    }
}
